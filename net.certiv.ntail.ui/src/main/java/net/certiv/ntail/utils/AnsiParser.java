package net.certiv.ntail.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.certiv.ntail.NTailPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Gerald B. Rosenberg
 */
public class AnsiParser {

	/**
	 * The control sequence introducer (CSI) is the two-character sequence: escape
	 * open-bracket; or, in unicode, the single character "\u009B", same as hex 9b. The
	 * escape character can be specified as "\u001B" in Java. The control sequence end
	 * (CSE) for graphic rendition codes is the lower-case letter "m". <br>
	 * <br>
	 * CSI [n [;k]] CSE <br>
	 * Sets SGR (Select Graphic Rendition) parameters. After CSI can be zero or more
	 * parameters separated with ;. With no parameters, CSI CSE is treated as CSI 0 CSE
	 * (reset / normal), which is typical of most of the ANSI codes. <br>
	 * <br>
	 * SGR parameters: graphics functions are specified by the following values and remain
	 * active until the next SGR control sequence.
	 * 
	 * <pre>
	 * Text attributes 
	 * 	 0    All attributes off 
	 * 	 1    Bold on 
	 * 	 3    Italic on
	 * 	 4    Underline single 
	 * 	 5    Blink on 
	 * 	 7    Reverse video on 
	 * 	 8    Concealed on 
	 * 	 9    Strikethrough on
	 * 	 21   Underline double
	 * 
	 * Foreground colors 
	 * 	 30   Black 
	 * 	 31   Red 
	 * 	 32   Green 
	 * 	 33   Yellow 
	 * 	 34   Blue 
	 * 	 35   Magenta 
	 * 	 36   Cyan 
	 * 	 37   White 
	 * 
	 * 	 90   Bright Black (dark grey)
	 * 	 91   Bright Red 
	 * 	 92   Bright Green 
	 * 	 93   Bright Yellow 
	 * 	 94   Bright Blue 
	 * 	 95   Bright Magenta 
	 * 	 96   Bright Cyan 
	 * 	 97   Bright White 
	 * 
	 * Background colors 
	 * 	 40   Black 
	 * 	 41   Red 
	 * 	 42   Green 
	 * 	 43   Yellow 
	 * 	 44   Blue 
	 * 	 45   Magenta 
	 * 	 46   Cyan 
	 * 	 47   White 
	 * 
	 * 	 100  Bright Black (dark grey)
	 * 	 101  Bright Red 
	 * 	 102  Bright Green 
	 * 	 103  Bright Yellow 
	 * 	 104  Bright Blue 
	 * 	 105  Bright Magenta 
	 * 	 106  Bright Cyan 
	 * 	 107  Bright White
	 * 
	 * Parameters 30 through 47 meet the ISO 6429 standard.
	 * 	
	 * </pre>
	 */

	private static final Color BLACK = new Color(Display.getCurrent(), 0, 0, 0);
	private static final Color RED = new Color(Display.getCurrent(), 255, 0, 0);
	private static final Color GREEN = new Color(Display.getCurrent(), 0, 255, 0);
	private static final Color YELLOW = new Color(Display.getCurrent(), 255, 255, 0);
	private static final Color BLUE = new Color(Display.getCurrent(), 0, 0, 255);
	private static final Color MAGENTA = new Color(Display.getCurrent(), 255, 0, 255);
	private static final Color CYAN = new Color(Display.getCurrent(), 0, 255, 255);
	private static final Color WHITE = new Color(Display.getCurrent(), 255, 255, 255);

	private static final String eol = System.getProperty("line.separator");
	private static final String CSI = "\u001B" + "[";
	private static final String rx = "\\e\\[m|\\e\\[(\\d+)m|\\e\\[(\\d+);(\\d+)m|\\e\\[(\\d+);(\\d+);(\\d+)m";
	private static final Pattern p = Pattern.compile(rx);

	/**
	 * Split a single line of the log text on each occurrence of a CSI. Process each
	 * partial line.
	 * 
	 * @param styledText
	 * @param text
	 */
	public static void processAnsiCodes(StyledText styledText, String line) {
		if (line.contains(CSI)) {
			Matcher m = p.matcher(line);
			int pdx = 0;
			int idx = 0;
			StyleRange sr = null;
			while (m.find()) {
				int beg = m.start();
				int end = m.end();

				// first, finish any prior segment
				if (beg > pdx) {
					idx = styledText.getCharCount();
					styledText.append(line.substring(pdx, beg));
					if (sr != null) {
						sr.start = idx;
						sr.length = beg - pdx;
						styledText.setStyleRange(sr);
					}
				}
				pdx = end;

				// now, process the current ansi code match
				sr = new StyleRange();
				for (int grp = 1; grp < m.groupCount(); grp++) {
					String s = m.group(grp);
					if (s != null) {
						int param = Integer.valueOf(s);
						sr = encodeStyle(sr, param);
					}
				}
			}
			// last, finish any trailing segment
			if (line.length() > pdx) {
				idx = styledText.getCharCount();
				styledText.append(line.substring(pdx));
				if (sr != null) {
					sr.start = idx;
					sr.length = line.substring(pdx).length();
					styledText.setStyleRange(sr);
				}
			}
			styledText.append(eol);
		} else {
			styledText.append(line + eol);
		}
	}

	private static StyleRange encodeStyle(StyleRange sr, int param) {
		switch (param) {
			case 0: // Reset
				sr.fontStyle = SWT.NORMAL;
				sr.underline = false;
				sr.strikeout = false;
				sr.foreground = BLACK;
				sr.background = WHITE;
				break;
			case 1: // Bold on
				sr.fontStyle += SWT.BOLD;
				break;
			case 3: // Italic on
				sr.fontStyle += SWT.ITALIC;
				break;
			case 4: // Underline single
				sr.underline = true;
				break;
			// case 5: // Blink on
			// break;
			// case 7: // Reverse video on
			// break;
			case 9: // Strikethrough on
				sr.strikeout = true;
				break;

			// Forground colors
			case 30: // Black
				sr.foreground = BLACK;
				break;
			case 31: // Red
				sr.foreground = RED;
				break;
			case 32: // Green
				sr.foreground = GREEN;
				break;
			case 33: // Yellow
				sr.foreground = YELLOW;
				break;
			case 34: // Blue
				sr.foreground = BLUE;
				break;
			case 35: // Magenta
				sr.foreground = MAGENTA;
				break;
			case 36: // Cyan
				sr.foreground = CYAN;
				break;
			case 37: // White
				sr.foreground = WHITE;
				break;

			// Background colors
			case 40: // Black
				sr.background = BLACK;
				break;
			case 41: // Red
				sr.background = RED;
				break;
			case 42: // Green
				sr.background = GREEN;
				break;
			case 43: // Yellow
				sr.background = YELLOW;
				break;
			case 44: // Blue
				sr.background = BLUE;
				break;
			case 45: // Magenta
				sr.background = MAGENTA;
				break;
			case 46: // Cyan
				sr.background = CYAN;
				break;
			case 47: // White
				sr.background = WHITE;
				break;
			default:
				NTailPlugin.getDefault().error("Unknown Ansi parameter [n=" + param + "]");
		}
		return sr;
	}

	// public void dispose() {
	// BLACK.dispose();
	// RED.dispose();
	// GREEN.dispose();
	// YELLOW.dispose();
	// BLUE.dispose();
	// MAGENTA.dispose();
	// CYAN.dispose();
	// WHITE.dispose();
	// }

	// private static boolean contains(String data, CharSequence c) {
	// char ch = c.charAt(0);
	// int idx = data.indexOf(ch);
	// if (idx == -1) return false;
	//
	// int len = c.length();
	// if (len == 1) return true;
	//
	// CharSequence cs = data.subSequence(idx, idx + len);
	// if (c.equals(cs)) {
	// return true;
	// }
	// return false;
	// }

	// /* Split the string data on ansi graphics codes, preserving all characters */
	// private static String[] split(String data, String c) {
	// ArrayList<String> al = new ArrayList<String>();
	// int pdx = data.length();
	// int idx = 0;
	// do {
	// idx = data.lastIndexOf(c, pdx - 1);
	// if (idx != -1) {
	// String s = data.substring(idx, pdx);
	// Matcher m = p.matcher(s.substring(2));
	// if (m.matches()) {
	// al.add(0, s.substring(m.end() - 2));
	// al.add(0, s.substring(0, m.end() - 2));
	// } else {
	// al.add(0, s);
	// }
	// pdx = idx;
	// }
	// } while (idx != -1);
	//
	// if (pdx > 0) {
	// al.add(0, data.substring(0, pdx));
	// }
	//
	// return al.toArray(new String[al.size()]);
	// }

	// private static void processAnsiPart(StyledText styledText, String cPart) {
	// Matcher m = p.matcher(cPart);
	// if (m.matches()) {
	// if (m.groupCount() == 0) {
	// styledText.append(cPart.substring(1));
	// } else {
	// int trim = 0;
	// StyleRange sr = new StyleRange();
	// for (int idx = 0; idx < m.groupCount(); idx++) {
	// trim += m.group(idx).length() + 1;
	// int param = Integer.parseInt(m.group(idx));
	// encodeStyle(sr, param);
	// }
	// String trimPart = cPart.substring(trim);
	// sr.start = styledText.getCharCount();
	// sr.length = trimPart.length();
	// styledText.append(trimPart);
	// styledText.setStyleRange(sr);
	// }
	// } else {
	// styledText.append(CSI + cPart);
	// }
	// }
}