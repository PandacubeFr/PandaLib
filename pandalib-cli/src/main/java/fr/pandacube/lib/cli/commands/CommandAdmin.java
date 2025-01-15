package fr.pandacube.lib.cli.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.chat.Chat.FormattableChat;
import fr.pandacube.lib.chat.ChatTreeNode;
import fr.pandacube.lib.cli.CLIApplication;
import fr.pandacube.lib.util.log.Log;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.pandacube.lib.chat.ChatStatic.chat;
import static fr.pandacube.lib.chat.ChatStatic.failureText;
import static fr.pandacube.lib.chat.ChatStatic.successText;
import static fr.pandacube.lib.chat.ChatStatic.text;

/**
 * The {@code admin} command for a {@link CLIApplication}.
 */
public class CommandAdmin extends CLIBrigadierCommand {

	/**
	 * Initializes the admin command.
	 */
	public CommandAdmin() {}

	@Override
	protected LiteralArgumentBuilder<CLICommandSender> buildCommand() {
		return literal("admin")
				.executes(this::version)
				.then(literal("version")
						.executes(this::version)
				)
				.then(literal("reload")
						.executes(this::reload)
				)
				.then(literal("debug")
						.executes(this::debug)
				)
				.then(literal("commandstruct")
						.executes(this::commandStruct)
						.then(argument("path", StringArgumentType.greedyString())
								.executes(this::commandStruct)
						)
				);
	}
	
	
	private int version(CommandContext<CLICommandSender> context) {
		Log.info(chat()
				.console(context.getSource().isConsole())
				.infoColor()
				.thenCenterText(text(CLIApplication.getInstance().getName()))
				.thenNewLine()
				.thenText("- Implem. version: ")
				.thenData(CLIApplication.getInstance().getClass().getPackage().getImplementationVersion())
				.thenNewLine()
				.thenText("- Spec. version: ")
				.thenData(CLIApplication.getInstance().getClass().getPackage().getSpecificationVersion())
				.getLegacyText());
		return 1;
	}
	
	
	
	private int reload(CommandContext<CLICommandSender> context) {
		CLIApplication.getInstance().reload();
		return 1;
	}
	
	private int debug(CommandContext<CLICommandSender> context) {
		Log.setDebug(!Log.isDebugEnabled());
		Log.info(successText("Mode débug "
				+ (Log.isDebugEnabled() ? "" : "dés") + "activé").getLegacyText());
		return 1;
	}
	
	private int commandStruct(CommandContext<CLICommandSender> context) {
		CLICommandSender sender = context.getSource();
		String[] tokens = tryGetArgument(context, "path", String.class, s -> s.split(" "), new String[0]);
		
		CommandNode<CLICommandSender> node = CLIBrigadierDispatcher.instance.getDispatcher().findNode(Arrays.asList(tokens));

		if (node == null) {
			Log.severe(failureText("La commande spécifiée n’a pas été trouvée.").getLegacyText());
			return 0;
		}

		Set<CommandNode<CLICommandSender>> scannedNodes = new HashSet<>();
		DisplayCommandNode displayNode = new DisplayCommandNode();

		// find parent nodes of scanned node to avoid displaying them after redirection and stuff
		for (int i = 1; i < tokens.length; i++) {
			CommandNode<CLICommandSender> ignoredNode = CLIBrigadierDispatcher.instance.getDispatcher().findNode(Arrays.asList(Arrays.copyOf(tokens, i)));
			if (ignoredNode != null) {
				displayNode.addInline(ignoredNode);
				scannedNodes.add(ignoredNode);
			}
		}

		buildDisplayCommandTree(displayNode, scannedNodes, node);

		ChatTreeNode displayTreeNode = buildDisplayTree(displayNode, sender);
		for (Chat comp : displayTreeNode.render(true))
			Log.info(comp.getLegacyText());
		return 1;
	}
	
	
	
	
	
	
	
	
	private void buildDisplayCommandTree(DisplayCommandNode displayNode, Set<CommandNode<CLICommandSender>> scannedNodes, CommandNode<CLICommandSender> node) {
		displayNode.addInline(node);
		
		scannedNodes.add(node);
		
		if (node.getRedirect() != null) {
			if (scannedNodes.contains(node.getRedirect()) || node.getRedirect() instanceof RootCommandNode) {
				displayNode.addInline(node.getRedirect());
			}
			else {
				buildDisplayCommandTree(displayNode, scannedNodes, node.getRedirect());
			}
		}
		else if (node.getChildren().size() == 1) {
			buildDisplayCommandTree(displayNode, scannedNodes, node.getChildren().iterator().next());
		}
		else if (node.getChildren().size() >= 2) {
			for (CommandNode<CLICommandSender> child : node.getChildren()) {
				DisplayCommandNode dNode = new DisplayCommandNode();
				buildDisplayCommandTree(dNode, scannedNodes, child);
				displayNode.addChild(dNode);
			}
		}
	}
	
	
	
	
	private ChatTreeNode buildDisplayTree(DisplayCommandNode displayNode, CLICommandSender sender) {
		Chat d = chat().then(displayCurrentNode(displayNode.nodes.get(0), false, sender));
		
		CommandNode<CLICommandSender> prevNode = displayNode.nodes.get(0);
		for (int i = 1; i < displayNode.nodes.size(); i++) {
			CommandNode<CLICommandSender> currNode = displayNode.nodes.get(i);
			if (currNode.equals(prevNode.getRedirect())) {
				d.then(text(" → ")
						.hover("Redirects to path: " + CLIBrigadierDispatcher.instance.getDispatcher().getPath(currNode))
				);
				d.then(displayCurrentNode(currNode, true, sender));
			}
			else {
				d.thenText(" ");
				d.then(displayCurrentNode(currNode, false, sender));
			}
			prevNode = currNode;
		}


		ChatTreeNode displayTree = new ChatTreeNode(d);
		
		for (DisplayCommandNode child : displayNode.children) {
			displayTree.addChild(buildDisplayTree(child, sender));
		}
		
		return displayTree;
		
	}
	
	
	
	
	
	private Component displayCurrentNode(CommandNode<CLICommandSender> node, boolean redirectTarget, CLICommandSender sender) {
		if (node == null)
			throw new IllegalArgumentException("node must not be null");
		FormattableChat d;
		if (node instanceof RootCommandNode) {
			d = text("(root)").italic()
				.hover("Root command node");
		}
		else if (node instanceof ArgumentCommandNode<?, ?> argNode) {
			ArgumentType<?> type = argNode.getType();
			String typeStr = type.getClass().getSimpleName();
			if (type instanceof IntegerArgumentType
					|| type instanceof LongArgumentType
					|| type instanceof FloatArgumentType
					|| type instanceof DoubleArgumentType) {
				typeStr = type.toString();
			}
			else if (type instanceof BoolArgumentType) {
				typeStr = "bool()";
			}
			else if (type instanceof StringArgumentType) {
				typeStr = "string(" + ((StringArgumentType) type).getType().name().toLowerCase() + ")";
			}
			String t = "<" + node.getName() + ">";
			String h = "Argument command node"
					+ "\nType: " + typeStr;

			if (node.getCommand() != null) {
				t += "®";
				h += "\nThis node has a command";
			}
			
			d = text(t);
			
			if (!node.canUse(sender)) {
				d.gray();
				h += "\nPermission not granted for you";
			}
			
			d.hover(h);
		}
		else if (node instanceof LiteralCommandNode) {
			String t = node.getName();
			String h = "Literal command node";

			if (node.getCommand() != null) {
				t += "®";
				h += "\nThis node has a command";
			}

			d = text(t);
			
			if (!node.canUse(sender)) {
				d.gray();
				h += "\nPermission not granted for you";
			}
			
			d.hover(h);
		}
		else {
			throw new IllegalArgumentException("Unknown command node type: " + node.getClass());
		}
		
		if (redirectTarget)
			d.gray();
		return d.get();
		
	}




	private static class DisplayCommandNode {
		final List<CommandNode<CLICommandSender>> nodes = new ArrayList<>();
		final List<DisplayCommandNode> children = new ArrayList<>();
		
		void addInline(CommandNode<CLICommandSender> node) {
			nodes.add(node);
		}
		
		void addChild(DisplayCommandNode child) {
			children.add(child);
		}
	}
	

}
