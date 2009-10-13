/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.microedition.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Date;

import com.nttdocomo.io.BufferedReader;


/**
 * �f�o�b�O�̃��[�e�B���e�B�N���X�ł��D �\�����x���̐ݒ�ŁA�W���G���[�o�͂ɕ\�����郁�b�Z�[�W�𐧌䂵�܂�.
 * 
 * <pre>
 *  �Q�Ƃ���V�X�e���v���p�e�B
 * 
 *   &quot;debug.stackDepth&quot;  �X�^�b�N�_���v�̐[���CVM �����ɂ���ĈႤ�D
 *                       3 �� J2SE for Windows �̒l�Ńf�t�H���g�D
 *   &quot;debug.level&quot;   �f�o�b�O���x��
 * </pre>
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.08a 021025 nsano kkyx version <br>
 *          0.09a 021211 nsano fix dump <br>
 *          0.10a 030114 nsano add withTime related <br>
 */
public class Debug {

    /**
     * ���S�Ƀf�o�b�O�R�[�h����菜���ꍇ�͈ȉ��� false �� ���Ă��ׂĂ��ăR���p�C�����Ă��������D
     */
    private static final boolean isDebug = true;

    // private static final boolean isDebug = false;

    // -------------------------------------------------------------------------

    /**
     * ���݃f�o�b�O���̃��x��
     * 
     * @see #level
     */
    public static final int SPECIAL = 0;

    /**
     * �G���[�̃��x��
     * 
     * @see #level
     */
    public static final int ERROR = 1;

    /**
     * �x���̃��x��
     * 
     * @see #level
     */
    public static final int WARNING = 2;

    /**
     * ����\�����郌�x��
     * 
     * @see #level
     */
    public static final int INFO = 3;

    /**
     * �f�o�b�O����\�����郌�x��
     * 
     * @see #level
     */
    public static final int DEBUG = 99;

    /**
     * ���ԏ���\������
     * 
     * @see #level
     */
    private static boolean withTime;

    /** �\�����郌�x�� (default:{@link kkyx.util.Debug#DEBUG} Level) */
    private static int level;

    /** �f�o�b�O���̏o�͐�X�g���[�� */
    private static PrintStream out = System.err;

    /** �X�^�b�N�g���[�X�������[�� */
    private static int depth;

    /**
     * ���������܂��D
     */
    static {
        depth = 3;
        level = DEBUG;
        withTime = false;
    }

    /**
     * �A�N�Z�X�ł��܂���D
     */
    private Debug() {
    }

    /**
     * �\�����郌�x����ݒ肵�܂��D
     * 
     * @param level �ݒ肷��\�����x��
     */
    public static final void setDebugLevel(int level) {
        if (isDebug) {
            Debug.level = level;
        }
    }

    /** */
    public static boolean isDebug() {
        return isDebug;
    }

    /** */
    private static void printTime() {
        out.print(new Date() + " ");
    }

    /**
     * �\�����x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂ɉ��s���Ń��b�Z�[�W���o�͂��܂��D
     * 
     * @param level ���̃��b�Z�[�W�̕\�����x��
     * @param message �\�����b�Z�[�W
     */
    public static final void println(int level, Object message) {
        if (isDebug && level <= Debug.level) {
            if (withTime) {
                printTime();
            }
            Context c = getContext(depth);
            out.println(getClassName(c.className) + "::" + c.methodName + ": " + message);
        }
    }

    /**
     * �\�����x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂ɉ��s���Ń��b�Z�[�W���o�͂��܂��D
     * 
     * @param level ���̃��b�Z�[�W�̕\�����x��
     * @param message �\�����b�Z�[�W
     */
    public static final synchronized void println(int level, boolean message) {
        if (isDebug) {
            depth++;
            println(level, String.valueOf(message));
            depth--;
        }
    }

    /**
     * �\�����x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂ɉ��s���Ń��b�Z�[�W���o�͂��܂��D
     * 
     * @param level ���̃��b�Z�[�W�̕\�����x��
     * @param message �\�����b�Z�[�W
     */
    public static final synchronized void println(int level, int message) {
        if (isDebug) {
            depth++;
            println(level, String.valueOf(message));
            depth--;
        }
    }

    /**
     * �f�o�b�O #Debug ���x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂ɉ��s���Ń��b�Z�[�W���o�͂��܂��D
     * 
     * @param message �\�����b�Z�[�W
     */
    public static final synchronized void println(Object message) {
        if (isDebug) {
            depth++;
            println(Debug.DEBUG, message);
            depth--;
        }
    }

    /**
     * �f�o�b�O #Debug ���x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂ɉ��s���Ń��b�Z�[�W���o�͂��܂��D
     * 
     * @param message �\�����b�Z�[�W
     */
    public static final synchronized void println(int message) {
        if (isDebug) {
            depth++;
            println(Debug.DEBUG, String.valueOf(message));
            depth--;
        }
    }

    /**
     * �f�o�b�O #Debug ���x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂ɉ��s���Ń��b�Z�[�W���o�͂��܂��D
     * 
     * @param message �\�����b�Z�[�W
     */
    public static final synchronized void println(boolean message) {
        if (isDebug) {
            depth++;
            println(Debug.DEBUG, String.valueOf(message));
            depth--;
        }
    }

    /**
     * �f�o�b�O #Debug ���x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂ɉ��s���Ń��b�Z�[�W���o�͂��܂��D
     * 
     * @param message �\�����b�Z�[�W
     */
    public static final synchronized void println(double message) {
        if (isDebug) {
            depth++;
            println(Debug.DEBUG, String.valueOf(message));
            depth--;
        }
    }

    /**
     * �\�����x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂Ƀ��b�Z�[�W���o�͂��܂�. �t�H�[�}�b�g�͈ȉ��̂悤�ɂȂ�܂��D
     * <p>
     * <i>�N���X��(�p�b�P�[�W��������)</i>::<i>���\�b�h��</i>: <i>���b�Z�[�W</i>
     * 
     * @param level ���̃��b�Z�[�W�̕\�����x��
     * @param message �\�����b�Z�[�W
     */
    public static final void print(int level, Object message) {
        if (isDebug && level <= Debug.level) {
            if (withTime) {
                printTime();
            }
            Context c = getContext(depth);
            out.print(getClassName(c.className) + "::" + c.methodName + ": " + message);
        }
    }

    /**
     * �f�o�b�O #Debug ���x����胁�b�Z�[�W���x�����Ⴂ���C �W���G���[�o�͂ɉ��s���Ń��b�Z�[�W���o�͂��܂��D
     * 
     * @param message �\�����b�Z�[�W
     */
    public static final synchronized void print(Object message) {
        if (isDebug) {
            depth++;
            print(Debug.DEBUG, message);
            depth--;
        }
    }

    /**
     * �p�b�P�[�W������菜�����N���X�����擾���܂��D Debug#print ���ł̂ݎg���Ă��������D isDebug �̍œK���������Ȃ��Ȃ�܂��D
     * 
     * @param name �p�b�P�[�W���̃N���X��
     * @return �p�b�P�[�W������菜�����N���X��
     */
    public static final String getClassName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * �X�g���[���� 16 �i���Ń_���v���܂��D
     */
    public static final void dump(InputStream is) {
        try {
            byte[] buf = new byte[16];
            boolean breakFlag = false;
            int m = 0;
            top: while (true) {
                for (int y = 0; y < 16; y++) {
                    for (int x = 0; x < 16; x++) {
                        int c = is.read();
                        if (c == -1) {
                            if (!breakFlag) {
                                breakFlag = true;
                                m = x;
                            }
                            if (m > 0)
                                out.print("   ");
                            else
                                break;
                        } else {
                            out.print(toHex2(c) + " ");
                            buf[x] = (byte) c;
                        }
                    }
                    for (int x = 0; x < 16; x++) {
                        if (breakFlag && x == m) {
                            out.println();
                            break top;
                        } else {
                            out.print(0x20 <= buf[x] && buf[x] <= 0x7e ? (char) buf[x] : '.');
                        }
                    }
                    out.println();
                }
                out.println();
            }
        } catch (IOException e) {
        }
    }

    /**
     * �擪�� 0 �Ŗ��߂� 2 ���̑啶���� 16 �i����Ԃ��܂��D
     */
    public static final String toHex2(int i) {
        String s = "0" + Integer.toHexString(i).toUpperCase();
        return s.substring(s.length() - 2);
    }

    /**
     * ���ݎ��s���̃v���O�����̃R���e�L�X�g�����擾���܂��D ���݂� JavaSoft �� Java2 SDK �ɂ����Ή����Ă��܂���D(���Ԃ�)
     * 
     * level �ɂ� 2 �ȏ���w�肷��D getContext() �𒼐ڌĂԏꍇ�� 2�C getContext() ���Ăԃ��\�b�h���Ăԏꍇ��
     * 3�C �̂悤�Ɏw�肷��B
     * <p>
     * �g�p�� 1�FgetContext() �𒼐ڌĂԏꍇ <tt><pre>
     * Context c = Debug.getContext(2);
     * System.err.println(&quot;���ݎ��s���̃��\�b�h��&quot; + c.methodName + &quot;�ł�&quot;);
     * </pre></tt>
     * <p>
     * �g�p�� 2�FgetContext()���ԐړI�ɌĂԏꍇ <tt><pre>
     * Context check() {
     *     Debug.getContext(3);
     * }
     * </pre></tt>
     * <p>
     * <tt><pre>
     * Context c = check();
     * System.err.println(&quot;���ݎ��s���̃��\�b�h��&quot; + c.methodName + &quot;�ł�&quot;);
     * </pre></tt>
     */
    private static final Context getContext(int level) {

        Context context = new Context();

        // printStackTrace()�̏o�͌��ʂ��擾����
        ByteArrayOutputStream out = new ByteArrayOutputStream();
//      PrintStream pout = new PrintStream(out);
        new Exception().printStackTrace();
        // new Exception().printStackTrace(System.err);

        // �~�����s��؂�o��
        BufferedReader din = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
        String targetLine = "  at CLASS.METHOD(FILE.java:0000)"; // �_�~�[
        try {
            for (int i = 0; i < level; i++) {
                din.readLine();
            }
            targetLine = din.readLine();

            // �؂�o�����s���g�[�N���ɕ���
            // System.err.println("Context::getContext: " + targetLine);
            StringTokenizer tk = new StringTokenizer(targetLine, " \n\t():,");
            String[] tokens = new String[5];
            int i;
            for (i = 0; tk.hasMoreTokens(); i++) {
                tokens[i] = tk.nextToken();
                // System.err.println("Context::getContext: token[" + i + "]" +
                // tokens[i]);
            }
            /*
             * if (i == 4) ; else if (i == 5) tokens[3] += " " + tokens[4]; else
             * return null;
             */
            if (i != 4 && i != 5)
                throw new IllegalArgumentException(targetLine);

            // Context�ɃZ�b�g
            // tokens[0]�� "at" �Ȃ̂Ŏ̂Ă�
            context.className = tokens[1].substring(0, tokens[1].lastIndexOf('.'));

            context.methodName = tokens[1].substring(tokens[1].lastIndexOf('.') + 1, tokens[1].length());
            context.sourceFile = tokens[2];
            try {
                context.lineNumber = Integer.parseInt(tokens[3]);
            } catch (NumberFormatException e) {
                // "Compiled Code", "Native Code" ��
                context.lineNumber = -1;
            }

            // System.err.println("Context::getContext: " + context.className);
            // System.err.println("Context::getContext: " + context.methodName);
            // System.err.println("Context::getContext: " + context.sourceFile);
            // System.err.println("Context::getContext: " + context.lineNumber);
        } catch (Exception e) {
            // System.err.println("Context::getContext: " + e);
            context.className = "Unknown";
            context.methodName = "unknown";
            context.sourceFile = "unknown";
            context.lineNumber = -1;
        }

        return context;
    }

    /**
     * �v���O�����̃R���e�L�X�g���̃N���X�ł��D
     */
    private static final class Context {
        public String className;

        public String methodName;

        public String sourceFile;

        public int lineNumber;

        public String toString() {
            return getClass().getName() + "[" + className + ", " + methodName + ", " + sourceFile + ", " + lineNumber + "]";
        }
    }
}

/* */
