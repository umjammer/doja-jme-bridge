/*
 * @(#)Font.java	1.35 02/08/14 @(#)
 *
 * Copyright (c) 1999-2002 Sun Microsystems, Inc.  All rights reserved.
 * PROPRIETARY/CONFIDENTIAL
 * Use is subject to license terms.
 */

package vavi.microedition.lcdui;


/**
 * The <code>Font</code> class represents fonts and font
 * metrics. <code>Fonts</code> cannot be
 * created by applications. Instead, applications query for fonts
 * based on
 * font attributes and the system will attempt to provide a font that
 * matches
 * the requested attributes as closely as possible.
 *
 * <p> A <code>Font's</code> attributes are style, size, and face. Values for
 * attributes must be specified in terms of symbolic constants. Values for
 * the style attribute may be combined using the bit-wise
 * <code>OR</code> operator,
 * whereas values for the other attributes may not be combined. For example,
 * the value </p>
 *
 * <p> <code>
 * STYLE_BOLD | STYLE_ITALIC
 * </code> </p>
 *
 * <p> may be used to specify a bold-italic font; however </p>
 *
 * <p> <code>
 * SIZE_LARGE | SIZE_SMALL
 * </code> </p>
 *
 * <p> is illegal. </p>
 *
 * <p> The values of these constants are arranged so that zero is valid for
 * each attribute and can be used to specify a reasonable default font
 * for the system. For clarity of programming, the following symbolic
 * constants are provided and are defined to have values of zero: </p>
 *
 * <p> <ul>
 * <li> <code> STYLE_PLAIN </code> </li>
 * <li> <code> SIZE_MEDIUM </code> </li>
 * <li> <code> FACE_SYSTEM </code> </li>
 * </ul> </p>
 *
 * <p> Values for other attributes are arranged to have disjoint bit patterns
 * in order to raise errors if they are inadvertently misused (for example,
 * using <code>FACE_PROPORTIONAL</code> where a style is
 * required). However, the values
 * for the different attributes are not intended to be combined with each
 * other. </p>
 * @since MIDP 1.0
 */

public final class Font {
    
    /** */
    com.nttdocomo.ui.Font nativeFont;

    /**
     * The plain style constant. This may be combined with the
     * other style constants for mixed styles. 
     *
     * <P>Value <code>0</code> is assigned to <code>STYLE_PLAIN</code>.</P>
     */
    public static final int STYLE_PLAIN = com.nttdocomo.ui.Font.STYLE_PLAIN;
  
    /**
     * The bold style constant. This may be combined with the
     * other style constants for mixed styles.
     *
     * <P>Value <code>1</code> is assigned to <code>STYLE_BOLD</code>.</P>
     */
    public static final int STYLE_BOLD = com.nttdocomo.ui.Font.STYLE_BOLD;
  
    /**
     * The italicized style constant. This may be combined with
     * the other style constants for mixed styles.
     *
     * <P>Value <code>2</code> is assigned to <code>STYLE_ITALIC</code>.</P>
     */
    public static final int STYLE_ITALIC = com.nttdocomo.ui.Font.STYLE_ITALIC;
  
    /**
     * The underlined style constant. This may be combined with
     * the other style constants for mixed styles.
     *
     * <P>Value <code>4</code> is assigned to <code>STYLE_UNDERLINED</code>.</P>
     */
    public static final int STYLE_UNDERLINED = 4; // TODO
  
    /**
     * The &quot;small&quot; system-dependent font size.
     *
     * <P>Value <code>8</code> is assigned to <code>STYLE_SMALL</code>.</P>
     */
    public static final int SIZE_SMALL = com.nttdocomo.ui.Font.SIZE_SMALL;
  
    /**
     * The &quot;medium&quot; system-dependent font size.
     *
     * <P>Value <code>0</code> is assigned to <code>STYLE_MEDIUM</code>.</P>
     */
    public static final int SIZE_MEDIUM = com.nttdocomo.ui.Font.SIZE_MEDIUM;
  
    /**
     * The &quot;large&quot; system-dependent font size.
     *
     * <P>Value <code>16</code> is assigned to <code>SIZE_LARGE</code>.</P>
     */
    public static final int SIZE_LARGE = com.nttdocomo.ui.Font.SIZE_LARGE;
  
    /**
     * The &quot;system&quot; font face.
     *
     * <P>Value <code>0</code> is assigned to <code>FACE_SYSTEM</code>.</P>
     */
    public static final int FACE_SYSTEM = com.nttdocomo.ui.Font.FACE_SYSTEM;
  
    /**
     * The &quot;monospace&quot; font face.
     *
     * <P>Value <code>32</code> is assigned to <code>FACE_MONOSPACE</code>.</P>
     */
    public static final int FACE_MONOSPACE = com.nttdocomo.ui.Font.FACE_MONOSPACE;
  
    /**
     * The &quot;proportional&quot; font face.
     *
     * <P>Value <code>64</code> is assigned to
     * <code>FACE_PROPORTIONAL</code>.</P>
     */
    public static final int FACE_PROPORTIONAL = com.nttdocomo.ui.Font.FACE_PROPORTIONAL;
  
    /**
     * Default font specifier used to draw Item and Screen contents.
     *
     * <code>FONT_STATIC_TEXT</code> has the value <code>0</code>.
     *
     * @see #getFont(int fontSpecifier)
     * @since MIDP 2.0
     */
    public static final int FONT_STATIC_TEXT = 0; // TODO

    /**
     * Font specifier used by the implementation to draw text input by
     * a user.
     *
     * <code>FONT_INPUT_TEXT</code> has the value <code>1</code>.
     *
     * @see #getFont(int fontSpecifier)
     * @since MIDP 2.0 
     */
    public static final int FONT_INPUT_TEXT = 1;

    /**
     * Gets the <code>Font</code> used by the high level user interface
     * for the <code>fontSpecifier</code> passed in. It should be used
     * by subclasses of
     * <code>CustomItem</code> and <code>Canvas</code> to match user
     * interface on the device.
     *
     * @param fontSpecifier one of <code>FONT_INPUT_TEXT</code>, or
     * <code>FONT_STATIC_TEXT</code>
     * @return font that corresponds to the passed in font specifier
     * @throws IllegalArgumentException if <code>fontSpecifier</code> is not 
     * a valid fontSpecifier
     * @since MIDP 2.0
     */
    public static Font getFont(int fontSpecifier) {

	Font font;

	switch (fontSpecifier) {
	case FONT_STATIC_TEXT:   // has the UE team defined RI fonts
	case FONT_INPUT_TEXT:    // for these two options? 
	    font = DEFAULT_FONT; 
	    break;
	default:
	    throw new IllegalArgumentException();
	}
	return font;
    }

    /**
     * Construct a new Font object
     *
     * @param face The face to use to construct the Font
     * @param style The style to use to construct the Font
     * @param size The point size to use to construct the Font
     */
    private Font(int face, int style, int size) {
        this.face  = face;
        this.style = style;
        this.size  = size;

        init(face, style, size);
    }

    /**
     * Gets the default font of the system.
     * @return the default font
     */
    public static Font getDefaultFont() {
        // SYNC NOTE: return of atomic value, no locking necessary
        DEFAULT_FONT.nativeFont = com.nttdocomo.ui.Font.getDefaultFont();
        return DEFAULT_FONT;
    }

    /**
     * Obtains an object representing a font having the specified face, style,
     * and size. If a matching font does not exist, the system will
     * attempt to provide the closest match. This method <em>always</em> 
     * returns
     * a valid font object, even if it is not a close match to the request. 
     *
     * @param face one of <code>FACE_SYSTEM</code>,
     * <code>FACE_MONOSPACE</code>, or <code>FACE_PROPORTIONAL</code>
     * @param style <code>STYLE_PLAIN</code>, or a combination of
     * <code>STYLE_BOLD</code>,
     * <code>STYLE_ITALIC</code>, and <code>STYLE_UNDERLINED</code>
     * @param size one of <code>SIZE_SMALL</code>, <code>SIZE_MEDIUM</code>,
     * or <code>SIZE_LARGE</code>
     * @return instance the nearest font found
     * @throws IllegalArgumentException if <code>face</code>, 
     * <code>style</code>, or <code>size</code> are not
     * legal values
     */
    public static Font getFont(int face, int style, int size) {
        if ((face != FACE_SYSTEM) 
            && (face != FACE_MONOSPACE)
            && (face != FACE_PROPORTIONAL)) {
            throw new IllegalArgumentException("Unsupported face");
        }

        if ((style & ((STYLE_UNDERLINED << 1) - 1)) != style) {
            throw new IllegalArgumentException("Illegal style");
        }

        if ((size != SIZE_SMALL) 
            && (size != SIZE_MEDIUM)
            && (size != SIZE_LARGE)) {
            throw new IllegalArgumentException("Unsupported size");
        }

        synchronized (Display.LCDUILock) {
            /* RFC: this makes garbage.  But hashtables need Object keys. */
            Integer key = new Integer(face | style | size);
            Font f = (Font) table.get(key);
            if (f == null) {
                f = new Font(face, style, size);
                table.put(key, f);
            }

            return f;
        }
    }

    /**
     * Gets the style of the font. The value is an <code>OR'ed</code>
     * combination of
     * <code>STYLE_BOLD</code>, <code>STYLE_ITALIC</code>, and
     * <code>STYLE_UNDERLINED</code>; or the value is
     * zero (<code>STYLE_PLAIN</code>).
     * @return style of the current font
     *
     * @see #isPlain()
     * @see #isBold()
     * @see #isItalic()
     */
    public int getStyle() {
        // SYNC NOTE: return of atomic value, no locking necessary
        return style;
    };

    /**
     * Gets the size of the font.
     *
     * @return one of <code>SIZE_SMALL</code>, <code>SIZE_MEDIUM</code>,
     * <code>SIZE_LARGE</code>
     */
    public int getSize() {
        // SYNC NOTE: return of atomic value, no locking necessary
        return size;
    }

    /**
     * Gets the face of the font.
     *
     * @return one of <code>FACE_SYSTEM</code>,
     * <code>FACE_PROPORTIONAL</code>, <code>FACE_MONOSPACE</code>
     */
    public int getFace() { 
        // SYNC NOTE: return of atomic value, no locking necessary
        return face;
    }

    /**
     * Returns <code>true</code> if the font is plain.
     * @see #getStyle()
     * @return <code>true</code> if font is plain
     */
    public boolean isPlain() {
        // SYNC NOTE: return of atomic value, no locking necessary
        return style == STYLE_PLAIN;
    }

    /**
     * Returns <code>true</code> if the font is bold.
     * @see #getStyle()
     * @return <code>true</code> if font is bold
     */
    public boolean isBold() {
        // SYNC NOTE: return of atomic value, no locking necessary
        return (style & STYLE_BOLD) == STYLE_BOLD;
    }
 
    /**
     * Returns <code>true</code> if the font is italic.
     * @see #getStyle()
     * @return <code>true</code> if font is italic
     */
    public boolean isItalic() {
        // SYNC NOTE: return of atomic value, no locking necessary
        return (style & STYLE_ITALIC) == STYLE_ITALIC;
    }

    /**
     * Returns <code>true</code> if the font is underlined.
     * @see #getStyle()
     * @return <code>true</code> if font is underlined
     */
    public boolean isUnderlined() {
        // SYNC NOTE: return of atomic value, no locking necessary
        return (style & STYLE_UNDERLINED) == STYLE_UNDERLINED;
    }
        
    /**
     * Gets the standard height of a line of text in this font. This value
     * includes sufficient spacing to ensure that lines of text painted this
     * distance from anchor point to anchor point are spaced as intended by the
     * font designer and the device. This extra space (leading) occurs below 
     * the text.
     * @return standard height of a line of text in this font (a 
     * non-negative value)
     */
    public int getHeight() {
        // SYNC NOTE: return of atomic value, no locking necessary
        return height;
    }

    /**
     * Gets the distance in pixels from the top of the text to the text's
     * baseline.
     * @return the distance in pixels from the top of the text to the text's
     * baseline
     */
    public int getBaselinePosition() {
        // SYNC NOTE: return of atomic value, no locking necessary
        return baseline;
    }

    /**
     * Gets the advance width of the specified character in this Font.
     * The advance width is the horizontal distance that would be occupied if
     * <code>ch</code> were to be drawn using this <code>Font</code>, 
     * including inter-character spacing following
     * <code>ch</code> necessary for proper positioning of subsequent text.
     * 
     * @param ch the character to be measured
     * @return the total advance width (a non-negative value)
     */
    public int charWidth(char ch) {
        return stringWidth("" + ch);
    }

    /**
     * Returns the advance width of the characters in <code>ch</code>, 
     * starting at the specified offset and for the specified number of
     * characters (length).
     * The advance width is the horizontal distance that would be occupied if
     * the characters were to be drawn using this <code>Font</code>,
     * including inter-character spacing following
     * the characters necessary for proper positioning of subsequent text.
     *
     * <p>The <code>offset</code> and <code>length</code> parameters must
     * specify a valid range of characters
     * within the character array <code>ch</code>. The <code>offset</code>
     * parameter must be within the
     * range <code>[0..(ch.length)]</code>, inclusive.
     * The <code>length</code> parameter must be a non-negative
     * integer such that <code>(offset + length) &lt;= ch.length</code>.</p>
     *
     * @param ch the array of characters
     * @param offset the index of the first character to measure
     * @param length the number of characters to measure
     * @return the width of the character range
     * @throws ArrayIndexOutOfBoundsException if <code>offset</code> and
     * <code>length</code> specify an
     * invalid range
     * @throws NullPointerException if <code>ch</code> is <code>null</code>
     */
    public int charsWidth(char[] ch, int offset, int length) {
    	return stringWidth(new String(ch, offset, length));
    }

    /**
     * Gets the total advance width for showing the specified
     * <code>String</code>
     * in this <code>Font</code>.
     * The advance width is the horizontal distance that would be occupied if
     * <code>str</code> were to be drawn using this <code>Font</code>, 
     * including inter-character spacing following
     * <code>str</code> necessary for proper positioning of subsequent text.
     * 
     * @param str the <code>String</code> to be measured
     * @return the total advance width
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     */
    public int stringWidth(java.lang.String str) {
        return nativeFont.stringWidth(str);
    }

    /**
     * Gets the total advance width for showing the specified substring in this
     * <code>Font</code>.
     * The advance width is the horizontal distance that would be occupied if
     * the substring were to be drawn using this <code>Font</code>,
     * including inter-character spacing following
     * the substring necessary for proper positioning of subsequent text.
     *
     * <p>
     * The <code>offset</code> and <code>len</code> parameters must
     * specify a valid range of characters
     * within <code>str</code>. The <code>offset</code> parameter must
     * be within the
     * range <code>[0..(str.length())]</code>, inclusive.
     * The <code>len</code> parameter must be a non-negative
     * integer such that <code>(offset + len) &lt;= str.length()</code>.
     * </p>
     *
     * @param str the <code>String</code> to be measured
     * @param offset zero-based index of first character in the substring
     * @param len length of the substring
     * @return the total advance width
     * @throws StringIndexOutOfBoundsException if <code>offset</code> and
     * <code>length</code> specify an
     * invalid range
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     */
    public native int substringWidth(String str, int offset, int len);


    // private implementation //

    /** The face of this Font */
    private int face;
    /** The style of this Font */
    private int style;
    /** The point size of this Font */
    private int size;
    /** The baseline of this Font */
    private int baseline;
    /** The height of this Font */
    private int height;

    /**
     * The "default" font, constructed from the 'system' face,
     * plain style, and 'medium' size point.
     */
    private static final Font DEFAULT_FONT = new Font(FACE_SYSTEM,
                                                      STYLE_PLAIN,
                                                      SIZE_MEDIUM);
    /**
     * A hashtable used to maintain a store of created Fonts
     * so they are not re-created in the future
     */
    private static java.util.Hashtable table = new java.util.Hashtable(4);

    /**
     * Natively initialize this Font object's peer
     *
     * @param face The face to initialize the native Font
     * @param style The style to initialize the native Font
     * @param size The point size to initialize the native Font
     */
    private void init(int face, int style, int size) {
        nativeFont = com.nttdocomo.ui.Font.getFont(face | style | size);
    }
}

