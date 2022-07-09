package fr.pandacube.lib.core.util;

//******************************************************************************
//***
//***		INTERPRETEUR ARITHMETIQUE version 2.1			Version Java
//***
//***
//***
//******************************************************************************

/*
Historique:

2.1:
  - Version Java disponible:
  # les operateurs mathematiques disponibles sont ceux de Java donc certains manquent.

2.0:
  - Portage en C++ et debut en Java

Version C++:

  - Meilleure gestion memoire lors de la construction de l'expression.
  - Acceleration de certaines operations.

Version Java:

  - Premiere version. Normalement ca doit marcher

1.3b: ajoute les fonctions suivantes: (NON DISTRIBUEE)
    - reconnaissance du symbole �
    - ecriture formatee d'expression (� ameliorer)

1.2: corrige les bugs suivants:
    - erreur sur l'interpretation de fonctions unaires imbriquees telles que ln(exp(x)).
    - la fonction puissance (pour les puissances entieres).
     ajoute:
    - la verification de la chaine entree (voir code d'erreur)
    - la verification de l'existence de l'evaluation (voir code d'erreur)


1.1: corrige un bug au niveau de l'interpretation des fonctions du type:

      exp(-(x+y))

1.0: premiere version

Le code source peut etre librement modifie et distribue.

Puisqu'il s'agit d'un  essai en Java, le code ne doit pas etre super genial et merite sans doute
quelques modifications.En particulier, il serait interessant de rajouter le support des Exceptions
pour les calculs (division par zero, etc...).

*/

// Classe servant à palier l'absence de passage par variables ou reference

class VariableInt {
	public int mValue;
}

// Classe principale

public class JArithmeticInterpreter {

	// Variables

	final int mOperator;
	final double mValue;
	JArithmeticInterpreter fg, fd;

	// Methods

	// ....................................................................................
	// Node

	private JArithmeticInterpreter() {
		this(0, 0);
	}

	// ....................................................................................
	// Node

	private JArithmeticInterpreter(int Operator, double Value) {
		mOperator = Operator;
		mValue = Value;
	}

	// ....................................................................................
	// Construct_Tree

	private static JArithmeticInterpreter constructTree(StringBuffer string, int length) {
		int imbric, Bimbric;
		int priorite, ope;
		int position, positionv, i, j;
		int opetemp = 0;
		int espa = 0, espat = 0;
		int caspp = 0;

		JArithmeticInterpreter node;

		// Initialisation des variables

		if (length <= 0) {
			return null;
		}

		ope = 0;
		imbric = 0;
		Bimbric = 128;
		priorite = 6;
		i = 0;
		positionv = position = 0;

		// Mise en place des donnees sur le morceau de chaine

		while (i < length)
			if (((string.charAt(i) > 47) && (string.charAt(i) < 58)) || (string.charAt(i) == '�')) {
				if (priorite > 5) {
					priorite = 5;
					positionv = i;
				}
				i++;
			}
			else if ((string.charAt(i) > 96) && (string.charAt(i) < 117)) {
				VariableInt Vopetemp, Vespat;

				Vopetemp = new VariableInt();
				Vespat = new VariableInt();

				Vopetemp.mValue = opetemp;
				Vespat.mValue = espat;

				FindOperator(Vopetemp, Vespat, string, i);

				opetemp = Vopetemp.mValue;
				espat = Vespat.mValue;

				if (opetemp >= 0) {
					if (imbric < Bimbric) {
						Bimbric = imbric;
						ope = opetemp;
						position = i;
						priorite = 4;
						espa = espat;
					}
					else if ((imbric == Bimbric) && (priorite >= 4)) {
						ope = opetemp;
						position = i;
						priorite = 4;
						espa = espat;
					}
					j = i + 1;
					i += espat;
					while (j < i)
						j++;

				}
				else if (string.charAt(i) == 't') {
					if (priorite == 6) ope = -1;
					i++;
				}
				else if (string.charAt(i) == 'p') {
					if (priorite == 6) ope = -2;
					i++;
				}
				else if (string.charAt(i) == 'r') {
					if (priorite == 6) ope = -2;
					i++;
				}
				else if (string.charAt(i) == 'n') {
					if (priorite == 6) ope = -1;
					i++;
				}
				else {
					return null;
				}
			}
			else
				switch (string.charAt(i)) {
				case '(':
					imbric++;
					i++;
					break;
				case ')':
					imbric--;
					i++;
					break;
				case '+':
					if (imbric < Bimbric) {
						Bimbric = imbric;
						priorite = 1;
						ope = 1;
						position = i;
						caspp = 0;
					}
					else if (imbric == Bimbric) {
						priorite = 1;
						ope = 1;
						position = i;
						caspp = 0;
					}
					i++;
					break;
				case '-':
					if (imbric < Bimbric) {
						if ((i - 1) < 0) {
							if (priorite > 5) {
								priorite = 1;
								position = i;
								ope = 2;
								Bimbric = imbric;
								caspp = 1;
							}
						}
						else if (string.charAt(i - 1) == '(') {
							if (priorite > 1) {
								priorite = 1;
								position = i;
								Bimbric = imbric;
								caspp = 1;
								ope = 2;
							}
						}
						else {
							Bimbric = imbric;
							priorite = 1;
							ope = 2;
							position = i;
							caspp = 0;
						}
					}
					else if (imbric == Bimbric) if ((i - 1) < 0) {
						if (priorite > 5) {
							priorite = 1;
							position = i;
							ope = 2;
							caspp = 1;
						}
					}
					else if (string.charAt(i - 1) == '(') {
						if (priorite > 1) {
							priorite = 1;
							position = i;
							caspp = 1;
							ope = 2;
						}
					}
					else {
						priorite = 1;
						ope = 2;
						position = i;
						caspp = 0;
					}
					i++;
					break;
				case '*':
					if (imbric < Bimbric) {
						Bimbric = imbric;
						priorite = 2;
						ope = 3;
						position = i;
					}
					else if ((imbric == Bimbric) && (priorite >= 2)) {
						priorite = 2;
						ope = 3;
						position = i;
					}
					i++;
					break;
				case '/':
					if (imbric < Bimbric) {
						Bimbric = imbric;
						priorite = 2;
						ope = 4;
						position = i;
					}
					else if ((imbric == Bimbric) && (priorite >= 2)) {
						priorite = 2;
						ope = 4;
						position = i;
					}
					i++;
					break;
				case '^':
					if (imbric < Bimbric) {
						Bimbric = imbric;
						priorite = 3;
						ope = 5;
						position = i;
					}
					else if ((imbric == Bimbric) && (priorite >= 3)) {
						priorite = 3;
						ope = 5;
						position = i;
					}
					i++;
					break;
				case '.':
					i++;
					break;
				default:
					return null;
				}

		if (imbric != 0) {
			return null;
		}

		// Traitement des donnees

		if (priorite == 6) {
			node = new JArithmeticInterpreter(ope, 0.0);
			return node;
		}
		else if (caspp == 1) {
			node = new JArithmeticInterpreter(2, 0);

			node.fg = new JArithmeticInterpreter(0, 0);
			node.fd = new JArithmeticInterpreter();

			if ((length - position - 1 - Bimbric) == 0) { // argument absent
				return null;
			}
			StringBuffer temp = CopyPartialString(string, (position + 1), (length - 1 - Bimbric));
			node.fd = constructTree(temp, (length - position - 1 - Bimbric));

			return node;
		}

		else if (priorite == 5) {
			node = new JArithmeticInterpreter(0, calc_const(string, positionv));

			return node;
		}
		else if (ope > 5) {
			node = new JArithmeticInterpreter(ope, 0);

			if ((length - position - espa - Bimbric) == 0) { // argument absent
				return null;
			}
			StringBuffer temp = CopyPartialString(string, (position + espa), (length - 1));
			node.fg = constructTree(temp, (length - position - espa - Bimbric));
			return node;
		}
		else {
			node = new JArithmeticInterpreter(ope, 0);

			if ((position - Bimbric) == 0) { // argument absent
				return null;
			}
			StringBuffer temp = CopyPartialString(string, Bimbric, (position - 1));
			node.fg = constructTree(temp, (position - Bimbric));
			if ((length - position - 1 - Bimbric) == 0) { // argument absent
				return null;
			}
			temp = CopyPartialString(string, (position + 1), (length - 1 - Bimbric));
			node.fd = constructTree(temp, (length - position - 1 - Bimbric));
			return node;
		}
	}

	// ....................................................................................

	private double computeTree() {
		if (mOperator == 0) return mValue;

		double valueL = fg.computeTree();

		double valueR = 0;

		if (fd != null) valueR = fd.computeTree();

		switch (mOperator) {
		case 1: // +
			return (valueL + valueR);
		case 2: // -
			return (valueL - valueR);
		case 3: // *
			return (valueL * valueR);
		case 4: // -
			if (valueR == 0) {
				return 0;
			}
			return (valueL / valueR);
		case 5: // ^
			return Math.pow(valueL, valueR);
		case 6: // exp
			return Math.exp(valueL);
		case 7: // ln
			if (valueL <= 0) {
				return 0;
			}
			return (Math.log(valueL) / Math.log(2));
		case 8: // log
			if (valueL <= 0) {
				return 0;
			}
			return Math.log(valueL);
		case 9: // sqrt
			if (valueL < 0) {
				return 0;
			}
			return Math.sqrt(valueL);
		case 10: // abs
			return Math.abs(valueL);
		case 11:
			return Math.sin(valueL); // sin
		case 12:
			return Math.cos(valueL); // cos
		case 13:
			return Math.tan(valueL); // tan
		case 14:
			return Math.asin(valueL); // asin
		case 15:
			return Math.acos(valueL); // acos
		case 16:
			return Math.atan(valueL); // atan
		default:
			return 0;
		}
	}

	// ....................................................................................
	// Write_Tree

	private void writeTree(StringBuffer string) {
		boolean parenthese = false;

		switch (mOperator) {
		case 0:
			string.append(StringUtil.formatDouble(mValue));
			break;
		case 1:
			fg.writeTree(string);
			string.append('+');
			fd.writeTree(string);
			break;
		case 2:
			if (fg.mOperator != 0 || fg.mValue != 0)
				fg.writeTree(string);
			string.append('-');
			if ((fd.mOperator == 1) || (fd.mOperator == 2)) {
				parenthese = true;
				string.append('(');
			}
			fd.writeTree(string);
			if (parenthese) string.append(')');
			break;
		case 3:
			if ((fg.mOperator == 1) || (fg.mOperator == 2)) {
				parenthese = true;
				string.append('(');
			}
			fg.writeTree(string);
			if (parenthese) string.append(')');
			parenthese = false;
			string.append('*');
			if ((fd.mOperator == 1) || (fd.mOperator == 2)) {
				parenthese = true;
				string.append('(');
			}
			fd.writeTree(string);
			if (parenthese) string.append(')');
			break;
		case 4:
			if ((fg.mOperator == 1) || (fg.mOperator == 2)) {
				parenthese = true;
				string.append('(');
			}
			fg.writeTree(string);
			if (parenthese) string.append(')');
			parenthese = false;
			string.append('/');
			if ((fd.mOperator > 0) && (fd.mOperator < 5)) {
				parenthese = true;
				string.append('(');
			}
			fd.writeTree(string);
			if (parenthese) string.append(')');
			break;
		case 5:
			if ((fg.mOperator > 0) && (fg.mOperator < 5)) {
				parenthese = true;
				string.append('(');
			}
			fg.writeTree(string);
			if (parenthese) string.append(')');
			parenthese = false;
			string.append('^');
			if ((fd.mOperator > 0) && (fd.mOperator < 5)) {
				parenthese = true;
				string.append('(');
			}
			fd.writeTree(string);
			if (parenthese) string.append(')');
			break;
		case 6:
			string.append("exp(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 7:
			string.append("log(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 8:
			string.append("ln(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 9:
			string.append("sqrt(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 10:
			string.append("|");
			fg.writeTree(string);
			string.append('|');
			break;
		case 11:
			string.append("sin(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 12:
			string.append("cos(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 13:
			string.append("tan(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 14:
			string.append("asin(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 15:
			string.append("acos(");
			fg.writeTree(string);
			string.append(')');
			break;
		case 16:
			string.append("atan(");
			fg.writeTree(string);
			string.append(')');
			break;
		}
	}

	// ....................................................................................
	// calc_const

	private static double calc_const(StringBuffer chaine, int pos) {
		int i = pos, j;
		double temp = 0;
		int signe = 1;
		int longueur = chaine.length();

		if (chaine.charAt(i) == '-') {
			signe = -1;
			i++;
		}
		if (chaine.charAt(i) == 'π') return signe * Math.PI;

		while (i < longueur && chaine.charAt(i) > 47 && chaine.charAt(i) < 58) {
			temp = temp * 10 + (chaine.charAt(i) - 48);
			i++;
		}
		if (i < longueur && chaine.charAt(i) == '.') {
			i++;
			j = 1;
			while (i < longueur && chaine.charAt(i) > 47 && chaine.charAt(i) < 58) {
				temp = temp + (chaine.charAt(i) - 48) * Math.exp(-j * 2.30258509);
				i++;
				j++;
			}
		}
		return (signe * temp);
	}

	// ....................................................................................
	// FindOperator

	private static void FindOperator(VariableInt oper, VariableInt esp, StringBuffer chaine, int pos) {
		switch (chaine.charAt(pos)) {
		case 'a':
			switch (chaine.charAt(pos + 1)) {
			case 'b':
				esp.mValue = 3;
				oper.mValue = 10;
				break;
			case 'c':
				esp.mValue = 4;
				oper.mValue = 15;
				break;
			case 's':
				esp.mValue = 4;
				oper.mValue = 14;
				break;
			case 't':
				esp.mValue = 4;
				oper.mValue = 16;
				break;
			}
			break;
		case 'c':
			if (chaine.charAt(pos + 1) == 'h') {
				esp.mValue = 2;
				oper.mValue = 18;
			}
			else if ((chaine.charAt(pos + 1) == 'o') && (chaine.charAt(pos + 2) == 's'))
				if (chaine.charAt(pos + 3) == 'h') {
				esp.mValue = 4;
				oper.mValue = 18;
			}
			else {
				esp.mValue = 3;
				oper.mValue = 12;
			}
			break;
		case 'e':
			if ((chaine.charAt(pos + 1) == 'x') && (chaine.charAt(pos + 2) == 'p')) {
				esp.mValue = 3;
				oper.mValue = 6;
			}
			else
				oper.mValue = -10;
			break;
		case 'l':
			if (chaine.charAt(pos + 1) == 'n') {
				esp.mValue = 2;
				oper.mValue = 7;
			}
			else if ((chaine.charAt(pos + 1) == 'o') && (chaine.charAt(pos + 2) == 'g')) {
				esp.mValue = 3;
				oper.mValue = 8;
			}
			else
				oper.mValue = -10;
			break;
		case 's':
			if (chaine.charAt(pos + 1) == 'h') {
				esp.mValue = 2;
				oper.mValue = 17;
			}
			else if (chaine.charAt(pos + 1) == 'q') {
				esp.mValue = 4;
				oper.mValue = 9;
			}
			else if (chaine.charAt(pos + 3) == 'h') {
				esp.mValue = 4;
				oper.mValue = 17;
			}
			else {
				esp.mValue = 3;
				oper.mValue = 11;
			}
			break;
		case 't':
			if (chaine.charAt(pos + 1) == 'h') {
				esp.mValue = 2;
				oper.mValue = 19;
			}
			else if ((chaine.charAt(pos + 1) == 'a') && (chaine.charAt(pos + 2) == 'n')) {
				if (chaine.charAt(pos + 3) == 'h') {
					esp.mValue = 4;
					oper.mValue = 19;
				}
				else {
					esp.mValue = 3;
					oper.mValue = 13;
				}
			}
			else
				oper.mValue = -10;
			break;
		default:
			oper.mValue = -10;
			break;
		}
	}

	// ....................................................................................
	// CopyPartialString

	private static StringBuffer CopyPartialString(StringBuffer chaine, int debut, int fin) {
		StringBuffer chartemp;
		int a = fin - debut + 1;
		chartemp = new StringBuffer(a + 1);

		for (int i = 0; i < a; i++)
			chartemp.append(chaine.charAt(debut + i));

		return chartemp;
	}

	public static double getResultFromExpression(String expr, StringBuffer writeTree) {
		StringBuffer input = new StringBuffer(expr);

		JArithmeticInterpreter jai = null;

		try {
			jai = JArithmeticInterpreter.constructTree(input, input.length());
		} catch (Exception ignored) {}

		if (jai == null) throw new IllegalArgumentException("Le calcul passé en paramètre est invalide");

		if (writeTree != null) {
			writeTree.setLength(0);
			jai.writeTree(writeTree);
		}

		return jai.computeTree();
	}

	public static double getResultFromExpression(String expr) {
		return getResultFromExpression(expr, null);
	}

	public static void main(String[] args) {

		StringBuffer b = new StringBuffer(0);

		String disp_res = StringUtil.formatDouble(JArithmeticInterpreter.getResultFromExpression("1245.25*2", b));

		System.out.println(disp_res);
		System.out.println(b);
	} // */

}
