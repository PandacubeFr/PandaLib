package fr.pandacube.lib.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import java.util.ArrayList;
import java.util.List;

/**
 * A tree structure of chat {@link Component} intended to be rendered in chat using {@link #render(boolean)}.
 */
public class ChatTreeNode {

    private static final String TREE_MIDDLE_CONNECTED = "├";
    private static final String TREE_END_CONNECTED = "└";
    private static final String TREE_MIDDLE_OPEN = "│§0`§r";
    private static final String TREE_END_OPEN = "§0```§r";
    private static final String TREE_MIDDLE_OPEN_CONSOLE = "│";
    private static final String TREE_END_OPEN_CONSOLE = " "; // nbsp


    /**
     * The component for the current node.
     */
    public final ComponentLike component;

    /**
     * Children nodes.
     */
    public final List<ChatTreeNode> children = new ArrayList<>();

    /**
     * Construct a new {@link ChatTreeNode}.
     * @param cmp the component for the current node.
     */
    public ChatTreeNode(ComponentLike cmp) {
        component = cmp;
    }

    /**
     * Adds a child to the current node.
     * @param child the child to add.
     * @return this.
     */
    public ChatTreeNode addChild(ChatTreeNode child) {
        children.add(child);
        return this;
    }

    /**
     * Generate a tree view based on this tree structure.
     * <p>
     * Each element in the returned list represent 1 line of this tree view.
     * Thus, the caller may send each line separately or at once, depending on the quantity of data.
     * @param console true to render for console, false otherwise.
     * @return a list of component, each element being a single line.
     */
    public List<Chat> render(boolean console) {
        List<Chat> ret = new ArrayList<>();

        ret.add(ChatStatic.chat()
                .then(component));

        for (int i = 0; i < children.size(); i++) {
            List<Chat> childComponents = children.get(i).render(console);
            boolean last = i == children.size() - 1;
            for (int j = 0; j < childComponents.size(); j++) {

                String prefix = last ? (j == 0 ? TREE_END_CONNECTED : (console ? TREE_END_OPEN_CONSOLE : TREE_END_OPEN))
                        : (j == 0 ? TREE_MIDDLE_CONNECTED : (console ? TREE_MIDDLE_OPEN_CONSOLE : TREE_MIDDLE_OPEN));

                ret.add(ChatStatic.text(prefix)
                        .then(childComponents.get(j)));
            }
        }


        return ret;
    }
}
