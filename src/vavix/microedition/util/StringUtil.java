/*
 * Copyright (c) 2004 by Naohide Sano, All Rights Reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.microedition.util;

import com.nttdocomo.ui.Font;
import com.nttdocomo.ui.Graphics;


/**
 * StringUtil.
 * 
 * @author <a href=mailto:vavivavi@yahoo.co.jp>Naohide Sano</a> (nsano)
 * @version 0.00 040208 nsano initial version <br>
 */
public final class StringUtil {

    /** */
    static int STR_TOP_ADJUST;

    /** */
    static int LINE_HEIGHT;

    /** */
    static Font font;

    /**
     * �������`�悵�܂��B\n �Ő܂�Ԃ��܂��B
     *
     * @param g �O���t�B�b�N�I�u�W�F�N�g
     * @param l �`�悷�镶
     * @param x �J�n x ���W
     * @param y �J�n y ���W
     * @param w �܂�Ԃ��� (pixel)
     * @param sl �J�n�s��
     * @param ml �ő�`��s��
     */
    public static void drawSentence(Graphics g,
                                    String l,
                                    int x,
                                    int y,
                                    int w,
                                    int sl,
                                    int ml) {
        int sp = 0;
        int p;
        int p2;
        int pn;
        int line = 0;

        pn = l.indexOf("\n");

        while (l.length() > sp) {
            p = font.getLineBreak(l, sp, l.length() - sp, w);

            if ((pn >= sp) && (pn <= p)) {
                p = pn;
                p2 = pn + 1;
                pn = l.indexOf("\n", p2);
            } else {
                p2 = p;
            }

            if (line >= sl) {
                g.drawString(l.substring(sp, p), x, y + STR_TOP_ADJUST);
                y += LINE_HEIGHT;

                if (--ml <= 0) {
                    return;
                }
            }

            line++;
            sp = p2;
        }
    }

    /**
     * drawSentence �ŏ����Ɖ��s�ɂȂ邩�𒲂ׂ܂��B
     *
     * @param l �`�悷�镶
     * @param w �܂�Ԃ���(pixel)
     * @return �s��
     */
    public static int getSentenceLine(String l, int w) {
        int sp = 0;
        int p;
        int p2;
        int pn;
        int line = 0;

        pn = l.indexOf("\n");

        while (l.length() > sp) {
            p = font.getLineBreak(l, sp, l.length() - sp, w);

            if ((pn >= sp) && (pn <= p)) {
                p = pn;
                p2 = pn + 1;
                pn = l.indexOf("\n", p2);
            } else {
                p2 = p;
            }

            line++;
            sp = p2;
        }

        return line;
    }

    /**
     *
     */
    public static String limitedString(String str, int width) {
        if (str.length() == 0) {
            return "";

            // SO �n�ł� str.length() == 0 �̂Ƃ���
            // substring �� Exception��f��
        }

        int limitedPos = font.getLineBreak(str, 0, str.length(), width);

        if (limitedPos == str.length()) {
            return str;
        } else {
            return (str.substring(0, limitedPos) + "..");
        }
    }

    /**
     *
     *
     */
    public static String fillNumber(int num, int digit, String fill) {
        String formatStr = Integer.toString(num);

        if (num < 0) {
            return formatStr;
        }

        if (formatStr.length() > digit) {
            return formatStr.substring(formatStr.length() - digit);
        }

        for (int i = formatStr.length(); i < digit; i++) {
            formatStr = fill + formatStr;
        }

        return formatStr;
    }
}

/* */
