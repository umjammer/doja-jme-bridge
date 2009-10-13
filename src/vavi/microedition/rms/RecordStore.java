/*
 * @(#)RecordStoreFile.java	1.40 02/10/03 @(#)
 *
 * Copyright (c) 2000-2002 Sun Microsystems, Inc.  All rights reserved.
 * PROPRIETARY/CONFIDENTIAL
 * Use is subject to license terms.
 */

package vavi.microedition.rms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;

import com.nttdocomo.io.ConnectionException;
import com.nttdocomo.lang.UnsupportedOperationException;


/**
 * �����[�h�Ńf�[�^�̕ۑ��p�Ɏg���X�N���b�`�p�b�h�̗̈�𓮓I�Ɏg�p���邽�߂̃N���X�B
 * MIDP��RecordStore�̃\�[�X�����ς��č쐬�������߁A�����̗ގ��_������܂��B�������e�ʍ팸�̂��߂Ɋ���̈Ⴂ������܂��B
 * 
 * �X�N���b�`�p�b�h�𕡐���Scratchpad�N���X�ŕ������Ĉ������Ƃ͂ł��܂���B
 * ���R�[�h�Ƃ��ĕۑ�����Ƃ��ɂ͑S��setRecord���\�b�h���g���܂��BRecordStore��addRecord���\�b�h�̂悤�Ȃ��̂͂���܂���B
 * RecordStore�̂悤��deleteRecord�����Ă����̔ԍ��͉i�v���Ԃɂ͂Ȃ�܂���BsetRecord���\�b�h���g���΍Ăюg����悤�ɂȂ�܂��B
 * RecordStore�̂悤��Listener���Z�b�g���邱�Ƃ͂���܂���B �X�V�񐔂�X�V�����Ƃ��������͕ێ�����܂���B
 * 
 * 
 * Scratchpad�N���X�Ńt�H�[�}�b�g���ꂽ�̈�͐擪��32byte�ɐ�����������f�[�^���i�[����A���̍\���͎��̂悤�ɂȂ��Ă��܂��B
 * 
 * Bytes - Usage 
 * 00-07 - �V�O�l�`��="docomosp"
 * 08-11 - �X�N���b�`�p�b�h�̑��e�� 
 * 12-15 - ���R�[�h�̑���
 * 16-19 - �ŏ��̃��R�[�h�̏ꏊ
 * 20-23 - �ŏ��̋󔒉ӏ��̏ꏊ
 * 24-27 - �f�[�^�̊J�n�ʒu
 * 28-31 - �f�[�^�̏I���ʒu
 * 
 * �܂��e���R�[�h�̐擪�ɂ�������������f�[�^��16byte�i�[����܂��B
 * 
 * �����̐������p���邱�Ƃō��̂悤�Ƀ��R�[�h���m���q�����Ƃœ��I�ȗ̈�m�ۂ��\�ɂ��Ă��܂��B
 * ����͎����ōs���邽�ߒʏ�͂������ӎ�����K�v�͂���܂��񂪁A���ۂ̃f�[�^���w�b�_��񕪂����]���ɏ���邱�Ƃ͊o���Ă����ĉ������B
 * ���̂܂܂ł��g���܂����ȉ��̒萔��ύX���邱�ƂōœK�����邱�Ƃ��ł���̂Ń\�[�X�𒼐ډ��ς��Ďg���ĉ������B
 * 
 * SP_CHACHE_SIZE 
 * SP_BLOCK_SIZE 
 * SP_COMPACTBUFFER_SIZE
 * 
 * ���̃N���X��6.5k(!)���x�̗e�ʂ�K�v�Ƃ��܂��B�ǂ����Ă����I�Ȋm�ۂ��K�v�łȂ��ꍇ�͎g��Ȃ��ق����ǂ��ł��傤:-)
 */
public class RecordStore {
    /**
     * �X�N���b�`�p�b�h�̃w�b�_���L���b�V���ł��鐔�ł��B
     * �L���b�V�����������ɑ��݂���ꍇ�̓��R�[�h��T�����삪�����Ȃ邽�ߓǂݏo��������������܂��B
     * �L���b�V���͔z��ł���A�e���R�[�h��ID % SP_CHACHE_SIZE�̈ʒu�Ɋi�[����܂��B
     * ����ėႦ�΂��̒l��10�Ȃ�΃��R�[�h0�ƃ��R�[�h10�͔r���I�ɂ����L���b�V���ł��܂���B
     * ���̒l��傫������΂��ꂾ��������������܂��̂ŗp�r�ɉ����čœK�Ȓl��ݒ肵�Ă��������B
     */
    private static int SP_CACHE_SIZE = 10;

    /**
     * �̈���m�ۂ��鎞�̒P��byte���ł�
     * �u���b�N���E���܂����ω����N�����Ƃ��ɂ͓����ł�╡�G�ȏ������������܂��B
     * ���̒l��]�T�������đ傫�����΂��̕ω��͋N����ɂ����Ȃ�܂����A�e�ʂ̖��ʂ͑����Ȃ�܂��B
     * �Ⴆ�΂��̒l��16�ŁA�������ރf�[�^��20byte�������Ƃ��A16*2=32�o�C�g�̗̈悪�m�ۂ���܂��B
     * ��Ƀf�[�^�̑傫�����ς��A16byte�ȉ���32byte�ȏ�ɂȂ������ɒʏ���傫�ȏ������������܂��B
     */
    private static int SP_BLOCK_SIZE = 16;

    /**
     * �󂫃u���b�N���l�߂�ۂɃf�[�^�̈ړ����s���܂����A����Ɏg����o�b�t�@�̃T�C�Y(byte)�ł�
     * �傫�����Έ�x�ɑ����̃f�[�^���ړ������邱�Ƃ��ł��邽�ߍ����ɂȂ�ł��傤�B
     * ���������܂�傫����肷����ƃ������e�ʃI�[�o�[�ɂȂ�\��������܂��B
     * ����ۂǑ傫�ȃf�[�^��p�ɂɏ������񂾂�������肵�Ȃ�������ɕύX����K�v�͂Ȃ��Ǝv���܂��B
     */
    private static int SP_COMPACTBUFFER_SIZE = 128;

    // ------------�ȉ��̃t�B�[���h�͂����炭�ύX���Ȃ������ǂ��ł��傤-------------------
    private static RecordStore spref = null;

    private static RecordHeaderCache recHeadCache = null;

    // �r���A�N�Z�X����p�̃I�u�W�F�N�g �e���\�b�h�͂��̃I�u�W�F�N�g�ɂ���ă��b�N����������
    Object rsLock;

    // Database�̃w�b�_�t�B�[���h 
    // �X�N���b�`�p�b�h�̑��e�� 
    private static int spTotalSize;

    // �L���ȃ��R�[�h�� 
    private static int spNumLiveRecords;

    // �ŏ��̃��R�[�h�ւ̃I�t�Z�b�g 
    private static int spFirstRecordOffset;

    // �ŏ��̋󂫃u���b�N�ւ̃I�t�Z�b�g
    private static int spFirstFreeBlockOffset;

    // �f�[�^�̊J�n�ʒu �ʏ��32�ɂȂ��Ă���
    private static int spDataStart;

    // �f�[�^�̏I���ʒu
    private static int spDataEnd;

    // ���ڃA�N�Z�X�p�̃I�t�Z�b�g�iSYSTEM_INFO + SP_...�j�̃A�h���X�ŃA�N�Z�X
    private final static int SP_HEADER_LENGTH = 32;

    private final static int SP_SIGNATURE = 0;

    private final static int SP_TOTAL_SIZE = 8;

    private final static int SP_NUM_LIVE = 12;

    private final static int SP_REC_START = 16;

    private final static int SP_FREE_START = 20;

    private final static int SP_DATA_START = 24;

    private final static int SP_DATA_END = 28;

    private String accessPath(int pos) {
        return "scratchpad:///0;pos=" + pos;
    }

    /**
     * Scratchpad�N���X���g���ăt�H�[�}�b�g���ꂽ�؂ɁA�X�N���b�`�p�b�h�̐擪��8�o�C�g�̃V�O�l�`�����ۑ�����܂��B
     * ascii�R�[�h��docomosp���Ӗ����Ă��܂��B
     */
    private static final byte[] FMT_TAG = new byte[] {
        (byte) 'd', (byte) 'o', (byte) 'c', (byte) 'o', (byte) 'm', (byte) 'o', (byte) 's', (byte) 'p'
    };

    /**
     * �t�H�[�}�b�g�̊J�n�ʒu�������Ă��܂��B
     * ����ȍ~�̑S�Ă̗̈��Scratchpad�N���X�Ńt�H�[�}�b�g����A���̐擪�̈ʒu�ɃV�O�l�`���ƃV�X�e����񂪕ۑ�����܂��B
     */
    private static final int SYSTEM_INFO = 0; // �V�X�e����񂪊i�[����Ă���ꏊ

    // Scratchpad : �X�N���b�`�p�b�h�̃C���X�^���X�����A�t�H�[�}�b�g����ǂݍ���
    /**
     * Bytes - Usage
     * 00-07 - �V�O�l�`��="docomosp"
     * 08-11 - �X�N���b�`�p�b�h�̑��e��
     * 12-15 - ���R�[�h�̑���
     * 16-19 - �ŏ��̃��R�[�h�̏ꏊ
     * 20-23 - �ŏ��̋󔒉ӏ��̏ꏊ
     * 24-27 - �f�[�^�̊J�n�ʒu
     * 28-31 - �f�[�^�̏I���ʒu
     */
    private RecordStore() {
        spref = this; // ���g�ւ̎Q�Ƃ�ۑ�
        recHeadCache = new RecordHeaderCache(SP_CACHE_SIZE); // �w�b�_�̃L���b�V�������
        rsLock = new Object(); // �r���A�N�Z�X����p�I�u�W�F�N�g�����
        recordListener = new Vector(3);
        try {
            synchronized (rsLock) {

                DataInputStream in = Connector.openDataInputStream(accessPath(SYSTEM_INFO));
                for (int i = 0; i < FMT_TAG.length; i++) {
                    if (FMT_TAG[i] != in.readByte()) {
                        in.close();
                        throw new RecordStoreException();
                        // �^�O�������ꍇ�͏�����
                    }
                    // ����Ȃ�Ώ����t�H�[�}�b�g����ǂ�
                    spTotalSize = in.readInt();
                    spNumLiveRecords = in.readInt();
                    spFirstRecordOffset = in.readInt();
                    spFirstFreeBlockOffset = in.readInt();
                    spDataStart = in.readInt();
                    spDataEnd = in.readInt();
                    in.close();
                }
            }
        } catch (Exception e) {
            deleteRecordStore(null);
        }
    }

    /**
     * ���̃N���X�̗B��̃C���X�^���X�𓾂܂��B
     * @return ���̃N���X�̃C���X�^���X��Ԃ��܂��B
     */
    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) {
        return spref == null ? new RecordStore() : spref;
    }

    /** */
    public static void deleteRecordStore(String recordStoreName) {
        spref.clear();
    }

    /**
     * �X�N���b�`�p�b�h�̓��e�����������܂��B
     * ���̃��\�b�h�̓C���X�^���X�����ꂽ�Ƃ��Ƀt�H�[�}�b�g���ُ킾�����ꍇ�͎����I�ɌĂ΂�܂��B
     * �����I�ɌĂԂ��Ƃ��ł��܂��B�X�N���b�`�p�b�h�ɋL�^���ꂽ�S�Ẵf�[�^�͏�������܂��B
     */
    private void clear() {
        synchronized (rsLock) {
            try {
                DataOutputStream out = Connector.openDataOutputStream(accessPath(SYSTEM_INFO));
                int _spTotalSize = 0;
                try {
                    while (true) {
                        out.writeByte(0);
                        _spTotalSize++;
                    } // �e�ʂ𐔂��S�Ă̒l��0�ŏ㏑������
                } catch (ConnectionException e) { // �������ݒl�I�[�o�[�i�����I���j
                    if (e.getStatus() == ConnectionException.SCRATCHPAD_OVERSIZE) {
                        spTotalSize = _spTotalSize;
                    }
                    out.close();
                }
                spNumLiveRecords = 0;
                spFirstRecordOffset = 0;
                spFirstFreeBlockOffset = 0;
                spDataStart = SP_HEADER_LENGTH;
                spDataEnd = SP_HEADER_LENGTH;
                write(FMT_TAG, SYSTEM_INFO);
                storeSPState();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    /**
     * �X�N���b�`�p�b�h�ւ̃A�N�Z�X����܂��B
     * �Ă�openScratchpad���\�b�h���ĂԂ܂ł̓A�N�Z�X���ł��܂���B
     * @throws RecordStoreException �I�����̃f�[�^����Ɉُ킪�������ꍇ�ɓ������܂��B
     */
    public void closeRecordStore()
        throws RecordStoreNotOpenException, RecordStoreException { 
        synchronized (rsLock) {
            if (spFirstFreeBlockOffset != 0) {
                compactRecords(); // �I������O�ɐ��ڂ���
            }
            spref = null; // ���g�ւ̎Q�Ƃ�p��
            recHeadCache = null; // ���R�[�h�w�b�_�̃L���b�V�����N���A
        }
    }

    /**
     * �Ώۂ̃��R�[�h���폜���܂��B
     * @param recordId �Ώۂ̃��R�[�h�ԍ��B
     * @throws InvalidRecordIDException �Ώۂ̔ԍ������݂��Ȃ������Ƃ��ɓ������܂��B
     */
    public void deleteRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        synchronized (rsLock) {
            RecordHeader rh = null;
            try {
                rh = findRecord(recordId, false);
                freeRecord(rh);
                recHeadCache.invalidate(rh.id);
            } catch (IOException ioe) {
                throw new RecordStoreException("error updating file after" + " record deletion");
            }       
            // �V�X�e�������X�V
            spNumLiveRecords--;
            storeSPState();
        }
    }

    /**
     * ���݂��郌�R�[�h�̑�����Ԃ��܂��B
     * @return ���݂��郌�R�[�h�̑����B
     */
    public int getNumRecords() throws RecordStoreNotOpenException {
        return spNumLiveRecords;
    }

    /**
     * �c��̋󂫗e�ʁibyte���j��Ԃ��܂��B
     * @return �󂫗e�ʁB
     */
    public int getSizeAvailable() throws RecordStoreNotOpenException {
        synchronized (rsLock) {
            try {
                int rest = spTotalSize - spDataEnd;
                int cur_offset = spFirstFreeBlockOffset;
                if (cur_offset == 0) {
                    return rest;
                }
                RecordHeader rh = new RecordHeader(cur_offset);
                while (cur_offset != 0) {
                    rh.load(cur_offset);
                    rest += rh.blockSize;
                    cur_offset = rh.dataLenOrNextFree;
                }
                return rest;
            } catch (IOException e) {
                return 0;
            }
        }
    }

    /**
     * Returns the data stored in the given record.
     *
     * @param recordId the ID of the record to use in this operation
     * @param buffer the byte array in which to copy the data
     * @param offset the index into the buffer in which to start copying
     *
     * @exception RecordStoreNotOpenException if the record store is
     *          not open
     * @exception InvalidRecordIDException if the recordId is invalid
     * @exception RecordStoreException if a general record store
     *          exception occurs
     * @exception ArrayIndexOutOfBoundsException if the record is
     *          larger than the buffer supplied
     *
     * @return the number of bytes copied into the buffer, starting at
     *          index <code>offset</code>
     * @see #setRecord
     */
    public int getRecord(int recordId, byte[] buffer, int offset) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        synchronized (rsLock) {
            RecordHeader rh;
            try {
                // throws InvalidRecordIDException
                rh = findRecord(recordId, true);
                rh.read(buffer, offset);
            } catch (IOException ioe) {
                throw new RecordStoreException("error reading record data");
            }
            return rh.dataLenOrNextFree;
        }
    }

    /**
     * �Ώۂ̃��R�[�h�Ɋi�[����Ă���f�[�^���o�C�g��Ŏ擾���܂��B
     * @param recordId �Ώۂ̃��R�[�h�ԍ��B
     * @return �Ώۂ̃��R�[�h�Ɋi�[����Ă���f�[�^�̃o�C�g��
     * @throws InvalidRecordIDException �Ώۂ����݂��Ȃ����A�f�[�^�̑傫����0�̏ꍇ�ɓ�������
     * �܂��͓ǂݍ��ݎ��ɉ��炩�̃G���[���������Ƃ��ɓ�������istatus=UNDEFINED�j
     */
    public byte[] getRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
//      int size = 0;
        byte[] data = null;
        try {
            // throws InvalidRecordIDException
            RecordHeader rh = findRecord(recordId, true);
            if (rh.dataLenOrNextFree == 0) {
                return null;
            }
            data = new byte[rh.dataLenOrNextFree];
            rh.read(data, 0);
        } catch (IOException ioe) {
            throw new RecordStoreException("error reading record data");
        }
        return data;
    }

    /**
     * �Ώۂ̃��R�[�h�Ɋi�[����Ă���f�[�^�̑傫���𓾂�B
     * @param recordId �Ώۂ̃��R�[�h�ԍ��B
     * @return �Ώۂ̃��R�[�h�̃f�[�^�̑傫���B
     * @throws InvalidRecordIDException �Ώۂ����݂��Ȃ��Ƃ��ɓ�������B
     */
    public int getRecordSize(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        synchronized (rsLock) {
            try {
                RecordHeader rh = findRecord(recordId, true);
                return rh.dataLenOrNextFree;
            } catch (java.io.IOException ioe) {
                throw new RecordStoreException("error reading record data");
            }
        }
    }

    /**
     * �X�N���b�`�p�b�h�̑��e�ʂ𓾂�B
     * note:clear()���\�b�h�̎��_�ő��e�ʂ͌��߂���B
     * 
     * @return �X�N���b�`�p�b�h�̑��e�ʁB
     */
    public int getSize() throws RecordStoreNotOpenException {
        return spTotalSize;
    }

    /**
     * �Ώۂ̃��R�[�h�Ƀf�[�^���������ށB ���Ƀ��R�[�h�����݂���ꍇ�ɂ͏㏑������܂��B
     * newData�̒�����offset����numBytes�������������܂�܂��B
     * �Ⴆ��newData��S�Ă��������ޏꍇ��(recordId, newData, 0,
     * newData.length)�̂悤�Ɉ���������Ă��������B
     * 
     * @param recordId �Ώۂ̃��R�[�h�ԍ��B
     * @param newData �������ރf�[�^�B
     * @param offset newData�����̕������i�񂾈ʒu���珑������
     * @param numBytes offset�Ŏw�肵���ʒu���炱�̕�������������
     * @throws RecordStoreFullException �T�C�Y�s���ŏ������߂Ȃ������ꍇ(status=SIZE_FULL)
     *             �������ݎ��ɕs���ȃG���[���o���ꍇ(status=UNDEFINED)
     */
    public void setRecord(int recordId, byte[] newData, int offset, int numBytes) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException {
        synchronized (rsLock) {

            if ((newData == null) && (numBytes > 0)) {
                throw new NullPointerException();
            }

            RecordHeader rh = null;
            RecordHeader newrh = null;

            try {
                rh = findRecord(recordId, false); // �Ώۂ̃��R�[�h��T��
            } catch (IOException ioe) {
                throw new RecordStoreException("error finding record data");
            }
            // ���ɑ��݂���ꍇ�͐V�������R�[�h���Â����R�[�h���傫�������������œ��삪�Ⴄ
            if (numBytes <= rh.blockSize - SP_REC_HEADER_LENGTH) {
                // �V�����f�[�^���u��������̃f�[�^��菬����
                int allocSize = getAllocSize(numBytes);
                if (rh.blockSize - allocSize >= SP_BLOCK_SIZE + SP_REC_HEADER_LENGTH) {
                    // �����o����]�T������Ȃ番��
                    splitRecord(rh, allocSize);
                }
                rh.dataLenOrNextFree = numBytes;
                try {
                    rh.store(); // �V�������R�[�h�w�b�_����������
                    recHeadCache.insert(rh); // �L���b�V���ɓo�^
                    if (newData != null) {
                        rh.write(newData, offset); // �f�[�^�̏�������
                    }
                } catch (java.io.IOException ioe) {
                    throw new RecordStoreException("error writing record" + " data");
                }
            } else {

                freeRecord(rh); // �Â��f�[�^���J��

                newrh = allocateNewRecordStorage(recordId, numBytes); // ���Ƃ����ėe�ʂ��m�ۂ���

                try {
                    if (newData != null) {
                        newrh.write(newData, offset);
                        recHeadCache.insert(newrh);
                    }
                } catch (IOException ioe) {
                    throw new RecordStoreException("error moving record " + "data");
                }
            }
            storeSPState();
        }
    }

    // ------------ �ȉ� private method --------------
    private byte[] read(int _offset, int _numBytes) throws IOException {
        byte[] bytes = new byte[_numBytes];
        DataInputStream in = Connector.openDataInputStream(accessPath(_offset));
        in.read(bytes, 0, _numBytes);
        in.close();
        return bytes;
    }

    private void write(byte[] _data, int _offset) throws IOException {
        DataOutputStream out = Connector.openDataOutputStream(accessPath(_offset));
        out.write(_data);
        out.close();
    }

    // allocateNewRecordStorage(id, dataSize) : dataSize�̑傫���̗e�ʂ��m�ۂ���
    private RecordHeader allocateNewRecordStorage(int _id, int _dataSize) throws RecordStoreException, RecordStoreFullException {
        int allocSize = getAllocSize(_dataSize); // �u���b�N�ɓK�����e�ʂ��m�ۂ���
        boolean foundBlock = false; // �u���b�N������������t���O�𗧂Ă�
        RecordHeader block = new RecordHeader();
        try {
            int offset = spFirstFreeBlockOffset; // �󂫃u���b�N����T��
            while (offset != 0) {
                block.load(offset);
                if (block.blockSize >= allocSize) { // �\���ȑ傫���̋󂫃u���b�N����������
                    foundBlock = true;
                    break; // ���̋󂫃u���b�N���g��
                }
                offset = block.dataLenOrNextFree;
            }
        } catch (IOException ioe) {
            throw new RecordStoreException("error finding first fit block");
        }

        // �\���ȑ傫���̋󂫃u���b�N�����������ꍇ
        if (foundBlock == false) {
            if (getSizeAvailable() < allocSize) { // ���X�󂫗e�ʂ�����Ȃ��ꍇ�͗�O�𓊂���
                throw new RecordStoreFullException();
            }
            // ���R�[�h�̖����ɐV�������R�[�h���m��
            block = new RecordHeader(spDataEnd, _id, spFirstRecordOffset, allocSize, _dataSize);
            try {
                block.store();
            } catch (IOException ioe) {
                throw new RecordStoreException("error writing " + "new record data"); 
            }
            spFirstRecordOffset = spDataEnd;
            spDataEnd += allocSize;
        } else {
            // block is where the new record should be stored
            if (block.id != -1) {
                throw new RecordStoreException("ALLOC ERR " + block.id + " is not a free block!");
            }

            removeFreeBlock(block); // remove from free block list

            block.id = _id;
            if (block.blockSize - allocSize >= SP_BLOCK_SIZE + SP_REC_HEADER_LENGTH) {
                splitRecord(block, allocSize); // sets block.blockSize
            }
            block.dataLenOrNextFree = _dataSize;
            try {
                block.store(); 
            } catch (java.io.IOException ioe) {
                throw new RecordStoreException("error writing free block " + "after alloc"); 
            }
        }
        // add new record to cache
        recHeadCache.insert(block);
        return block;
    }

    /**
     * �w�b�_��������
     * @throws InvalidRecordIDException �Ȃ���Γ�����
     */
    private RecordHeader findRecord(int _recordId, boolean _addToCache) throws InvalidRecordIDException, IOException {
        RecordHeader rh;
        int cur_offset = spFirstRecordOffset;
        if (cur_offset == 0) { // ���R�[�h���P�����݂��Ȃ��ꍇ
            throw new InvalidRecordIDException();
        }
        rh = recHeadCache.get(_recordId); // �L���b�V���ɂ���ꍇ�͂����Ԃ�

        if (rh != null) {
            return rh;
        } // �����T��

        rh = new RecordHeader();
        while (cur_offset != 0) {
            rh.load(cur_offset);
            if (rh.id == _recordId) {
                break;
            } else {
                cur_offset = rh.nextOffset;
            }
        }
        if (cur_offset == 0) { // �T������������Ȃ������ꍇ
            throw new InvalidRecordIDException();
        }
        if (_addToCache) {
            recHeadCache.insert(rh);
        }
        return rh;
    }

    /**
     * ���R�[�h���J������
     */
    private void freeRecord(RecordHeader _rh) throws RecordStoreException {
        if (_rh.offset == spFirstRecordOffset) { // �P�Ԑ擪�̃��R�[�h���J������ꍇ
            spFirstRecordOffset = _rh.nextOffset; // ���̃��R�[�h��擪��
            spDataEnd = _rh.offset; // ���̃��R�[�h���������ʒu���I�[�ɂ���
        } else {
            _rh.id = FREE_BLOCK; // id���󂫃u���b�N�ɐݒ�
            _rh.dataLenOrNextFree = spFirstFreeBlockOffset;
            spFirstFreeBlockOffset = _rh.offset; // ���̃��R�[�h���󂫃u���b�N�̐擪�ɂ���
            try {
                _rh.store();
            } catch (IOException ioe) {
                throw new RecordStoreException("free record failed");
            }
        }
    }

    /** �m�ۂ���e�ʂ�Ԃ� */
    private int getAllocSize(int _numBytes) {
        int rv;
        int pad;
        rv = SP_REC_HEADER_LENGTH + _numBytes;
        pad = SP_BLOCK_SIZE - (rv % SP_BLOCK_SIZE);
        if (pad != SP_BLOCK_SIZE) {
            rv += pad;
        }
        return rv;
    }

    /** �󂫃u���b�N�̃w�b�_�����폜���� */
    private void removeFreeBlock(RecordHeader _blockToFree) throws RecordStoreException {
        RecordHeader block = new RecordHeader();
        RecordHeader prev = new RecordHeader();
        RecordHeader tmp = null;
        try {
        int offset = spFirstFreeBlockOffset;
            while (offset != 0) {
                block.load(offset);
                if (block.offset == _blockToFree.offset) {
                    if (block.id != -1) {
                        throw new IOException();
                    }
                    if (prev.offset == 0) {
                        spFirstFreeBlockOffset = block.dataLenOrNextFree;
                    } else {
                        prev.nextOffset = block.dataLenOrNextFree;
                        prev.store();
                    }
                }
                offset = block.dataLenOrNextFree;
                tmp = prev;
                prev = block;
                block = tmp;
            }
        } catch (IOException ioe) {
            throw new RecordStoreException("removeFreeBlock block not found");
        }   
    }

    /**
     * �󂫃u���b�N�̐擪allocSize�������R�[�h�ɁA�c����󂫃u���b�N�ɂ���
     */
    private void splitRecord(RecordHeader _recHead, int _allocSize) throws RecordStoreException {
        RecordHeader newfb;
        int extraSpace = _recHead.blockSize - _allocSize;
        int oldBlockSize = _recHead.blockSize;
        _recHead.blockSize = _allocSize;
        if (_recHead.offset != spFirstRecordOffset) {
            int fboffset = _recHead.offset + _allocSize;
            newfb = new RecordHeader(fboffset, -1, _recHead.offset, extraSpace, 0);
            try {
                freeRecord(newfb);
                RecordHeader prh = new RecordHeader(_recHead.offset + oldBlockSize);
                prh.nextOffset = fboffset;
                prh.store();
                recHeadCache.invalidate(prh.id);
                storeSPState();
            } catch (IOException ioe) {
                throw new RecordStoreException("splitRecord error");
            }
        } else {
            spDataEnd = _recHead.offset + _recHead.blockSize;
        }
    }

    /** */
    private void storeSPState() throws RecordStoreException {
        try {
            // set modification time
            dbLastModified = System.currentTimeMillis();
            DataOutputStream out = Connector.openDataOutputStream(accessPath(SYSTEM_INFO + FMT_TAG.length));
            out.writeInt(spTotalSize);
            out.writeInt(spNumLiveRecords);
            out.writeInt(spFirstRecordOffset);
            out.writeInt(spFirstFreeBlockOffset);
            out.writeInt(spDataStart);
            out.writeInt(spDataEnd);
            out.close();
        } catch (IOException ioe) {
            throw new RecordStoreException("error writing record store " + "attributes");
        }
    }

    /**
     * �S�Ă̋󂫃��R�[�h���l�߂Đ��ڂ���
     */
    private void compactRecords() throws RecordStoreNotOpenException, RecordStoreException {
        int offset = spDataStart; // �ʏ�̃��R�[�h�w�b�_�T���Ƃ͈Ⴂ�A���R�[�h�̐擪����T������
        int target = 0;
        int bytesLeft;
        int numToMove;
        byte[] chunkBuffer = new byte[SP_COMPACTBUFFER_SIZE]; // �o�b�t�@
        RecordHeader rh = new RecordHeader();
        int prevRec = 0;
        while (offset < spDataEnd) { // �T�����I�[�܂ŒB���Ă��Ȃ��Ȃ�
            try {
                rh.load(offset); // �w�b�_�����[�h����
            } catch (IOException ioe) {
                // NOTE - should throw some exception here
                System.out.println("Unexpected IOException in CompactRS!");
            }
            if (rh.id == FREE_BLOCK) { // ���[�h�����w�b�_���󂫃u���b�N�̏ꍇ
                if (target == 0) {
                    target = offset; // ���̃w�b�_�̈ʒu��target�ɂ���
                }
                offset += rh.blockSize; // ���̃u���b�N�ɐi��
            } else { // �f�[�^�u���b�N�̏ꍇ
                if (target == 0) { // ����܂ł̒T���ŋ󂫃u���b�N�������ꍇ�͓������K�v���Ȃ�
                    prevRec = offset;
                    offset += rh.blockSize;
                } else {
                    int old_offset = target; // target�̈ʒu��old_offset�ɕۑ�
                    // Move a record back in the file
                    rh.offset = target; // ���̃��R�[�h�w�b�_�̈ʒu�����ړ���̋󂫃u���b�N�̈ʒu�ɕύX
                    rh.nextOffset = prevRec;
                    try {
                        rh.store(); // �܂��w�b�_������������
                        offset += SP_REC_HEADER_LENGTH;
                        target += SP_REC_HEADER_LENGTH;
                        bytesLeft = (rh.blockSize - SP_REC_HEADER_LENGTH);
                        while (bytesLeft > 0) {
                            if (bytesLeft < SP_COMPACTBUFFER_SIZE) {
                                numToMove = bytesLeft;
                            } else {
                                numToMove = SP_COMPACTBUFFER_SIZE;
                            }
                            chunkBuffer = read(offset, numToMove);
                            write(chunkBuffer, target);
                            offset += numToMove;
                            target += numToMove;
                            bytesLeft -= numToMove;
                        }
                    } catch (IOException ioe) {
                        System.out.println("Unexpected IOException " + "in CompactRS!");
                    }
                    prevRec = old_offset;
                }
            }
        }
        if (rh.offset != 0) {
            spDataEnd = rh.offset + rh.blockSize; // �f�[�^�I�[�̈ʒu���C��
        }
        spFirstRecordOffset = rh.offset; // ���R�[�h�J�n�ʒu���C��
        spFirstFreeBlockOffset = 0; // �S�Ă̋󂫃u���b�N�͖����Ȃ���
        storeSPState();
    }

    /****************************************************************************
     * RecordHeader : �e���R�[�h�̃w�b�_
     ***************************************************************************/

    /*
     * Bytes - Usage
     * 00-03 - ���R�[�hID�i�󂫃u���b�N�ł�-1�j
     * 04-07 - ���̃��R�[�h�܂ł̃I�t�Z�b�g
     * 08-11 - ���R�[�h�̗e��
     * 12-15 - �f�[�^�̃T�C�Y�i�󂫃u���b�N�ł͎��̋󂫃u���b�N�܂ł̃I�t�Z�b�g�j
     * 16-xx - �f�[�^
     */
    private final static int SP_REC_HEADER_LENGTH = 16;
    private final static int REC_ID = 0;
    private final static int REC_NEXT_OFFSET = 4;
    private final static int REC_BLOCK_SIZE = 8;
    private final static int REC_DATA_LEN = 12;
    private final static int REC_DATA_OFFSET = 16;

    // �f�[�^�̎n�܂�
    private final static int FINAL_BLOCK = 0;
    private final static int FREE_BLOCK = -1;

    /**
     * �e���R�[�h�̃w�b�_�̐�����������N���X�ł��B
     * ����̓X�N���b�`�p�b�h���̈ʒu�A���̃��R�[�h�ւ̃I�t�Z�b�g�A�i�[����Ă���f�[�^�̒����A���̃��R�[�h�̃T�C�Y��
     * ���������AScratchpad�͂��̏���p���ă��R�[�h�����̂悤�Ɍq���ł��܂��B
     */
    private class RecordHeader {
        int offset;
        int id;
        int nextOffset;
        int blockSize;
        int dataLenOrNextFree;

        RecordHeader() {
        }

        RecordHeader(int _offset) throws IOException {
            load(_offset);
        }

        RecordHeader(int _offset, int _id, int _nextOffset, int _blockSize, int _dataLen) {
            offset = _offset;
            id = _id;
            nextOffset = _nextOffset;
            blockSize = _blockSize;
            dataLenOrNextFree = _dataLen;
        }

        // load(_offset) : _offset�̈ʒu�ɂ��郌�R�[�h�w�b�_��ǂݍ���
        void load(int _offset) throws IOException {
            DataInputStream in = Connector.openDataInputStream(accessPath(_offset));
            offset = _offset;
            id = in.readInt();
            nextOffset = in.readInt();
            blockSize = in.readInt();
            dataLenOrNextFree = in.readInt();
            in.close();
        }

        // store : ���R�[�h�w�b�_��Scratchpad�ɏ������ށB
        void store() throws IOException {
            DataOutputStream out = Connector.openDataOutputStream(accessPath(offset));
            out.writeInt(id);
            out.writeInt(nextOffset);
            out.writeInt(blockSize);
            out.writeInt(dataLenOrNextFree);
            out.close();
        }

        // read(buf, _offset) : �p�ӂ����o�b�t�@buf��_offset�Ŏw�肵���ʒu�ȍ~��ǂݍ���
        int read(byte[] buf, int _offset) throws IOException {
            InputStream in = Connector.openInputStream(accessPath(offset + REC_DATA_OFFSET));
            int len = in.read(buf, _offset, dataLenOrNextFree);
            in.close();
            return len;
        }

        // write(data, _offset) : _offset�̈ʒu����f�[�^data����������
        void write(byte[] data, int _offset) throws IOException {
            OutputStream out = Connector.openOutputStream(accessPath(offset + REC_DATA_OFFSET));
            out.write(data, _offset, dataLenOrNextFree);
            out.close();
        }
    }

    /**
     * ���̃N���X�͓�����RecordHeader�̔z���ێ����܂��B
     * Scratchpad�N���X�͂܂����̃N���X�ɃA�N�Z�X���A�Ώۂ�
     * ���R�[�h���L���b�V������Ă��邩�ǂ������`�F�b�N���A����������X�N���b�`�p�b�h������T���܂��B
     */
    private class RecordHeaderCache {
        private RecordHeader[] cache;

        public RecordHeaderCache(int _size) {
            cache = new RecordHeader[_size];
        }

        public RecordHeader get(int _recordId) {
            for (int i = 0; i < cache.length; i++) {
                if (cache[i] != null && cache[i].id == _recordId) {
                    return cache[i];
                }
            }
            return null;
        }

        // insert(rh) : �L���b�V���ɉ�����
        public void insert(RecordHeader _rh) {
            int idx = _rh.id % cache.length;
            cache[idx] = _rh;
        }

        // invalidate(rec_id) : �L���b�V������Ă��������
        void invalidate(int _rec_id) {
            if (_rec_id > 0) {
                int idx = _rec_id % cache.length;
                if ((cache[idx] != null) && (cache[idx].id == _rec_id)) {
                    cache[idx] = null;
                }
            }
        }
    }

    /**
     * Returns an array of the names of record stores owned by the
     * MIDlet suite. Note that if the MIDlet suite does not
     * have any record stores, this function will return null.
     *
     * The order of RecordStore names returned is implementation
     * dependent.
     *
     * @return array of the names of record stores owned by the
     * MIDlet suite. Note that if the MIDlet suite does not
     * have any record stores, this function will return null.
     */
    public static String[] listRecordStores() {
        return new String[] { "ScratchPad" };
    }

    /**
     * Returns the name of this RecordStore.
     *
     * @return the name of this RecordStore
     *
     * @exception RecordStoreNotOpenException if the record store is not open
     */
    public String getName() throws RecordStoreNotOpenException {
        return "ScratchPad";
    }

    /**
     * Authorization to allow access only to the current MIDlet
     * suite. AUTHMODE_PRIVATE has a value of 0.
     */
    public final static int AUTHMODE_PRIVATE = 0;

    /**
     * Authorization to allow access to any MIDlet
     * suites. AUTHMODE_ANY has a value of 1.
     */
    public final static int AUTHMODE_ANY = 1;

    /**
     * Changes the access mode for this RecordStore. The authorization
     * mode choices are:
     *
     * <ul>
     * <li><code>AUTHMODE_PRIVATE</code> - Only allows the MIDlet
     *          suite that created the RecordStore to access it. This
     *          case behaves identically to
     *          <code>openRecordStore(recordStoreName,
     *          createIfNecessary)</code>.</li>
     * <li><code>AUTHMODE_ANY</code> - Allows any MIDlet to access the
     *          RecordStore. Note that this makes your recordStore
     *          accessible by any other MIDlet on the device. This
     *          could have privacy and security issues depending on
     *          the data being shared. Please use carefully.</li>
     * </ul>
     *
     * <p>The owning MIDlet suite may always access the RecordStore and
     * always has access to write and update the store. Only the
     * owning MIDlet suite can change the mode of a RecordStore.</p>
     *
     * @param authmode the mode under which to check or create access.
     *      Must be one of AUTHMODE_PRIVATE or AUTHMODE_ANY.
     * @param writable true if the RecordStore is to be writable by
     *      other MIDlet suites that are granted access
     *
     * @exception RecordStoreException if a record store-related
     *      exception occurred
     * @exception SecurityException if this MIDlet Suite is not
     *      allowed to change the mode of the RecordStore
     * @exception IllegalArgumentException if authmode is invalid
     * @since MIDP 2.0
     */
    public void setMode(int authmode, boolean writable) throws RecordStoreException {
        throw new UnsupportedOperationException();
    }


    /**
     * Each time a record store is modified (by
     * <code>addRecord</code>, <code>setRecord</code>, or
     * <code>deleteRecord</code> methods) its <em>version</em> is
     * incremented. This can be used by MIDlets to quickly tell if
     * anything has been modified.
     *
     * The initial version number is implementation dependent.
     * The increment is a positive integer greater than 0.
     * The version number increases only when the RecordStore is updated.
     *
     * The increment value need not be constant and may vary with each
     * update.
     *
     * @return the current record store version
     *
     * @exception RecordStoreNotOpenException if the record store is
     *            not open
     */
    public int getVersion() throws RecordStoreNotOpenException {
        return dbVersion;
    }

    /** time record store was last modified (in milliseconds */
    private long dbLastModified;

    /**
     * Returns the last time the record store was modified, in the
     * format used by System.currentTimeMillis().
     *
     * @return the last time the record store was modified, in the
     *      format used by System.currentTimeMillis()
     *
     * @exception RecordStoreNotOpenException if the record store is
     *          not open
     */
    public long getLastModified() throws RecordStoreNotOpenException {
        return dbLastModified;
    }

    /** recordListeners of this record store */
    private Vector recordListener;

    /**
     * Adds the specified RecordListener. If the specified listener
     * is already registered, it will not be added a second time.
     * When a record store is closed, all listeners are removed.
     *
     * @param listener the RecordChangedListener
     * @see #removeRecordListener
     */
    public void addRecordListener(RecordListener listener) {
        synchronized (rsLock) {
            if (!recordListener.contains(listener)) {
                recordListener.addElement(listener);
            }
        }
    }

    /**
     * Removes the specified RecordListener. If the specified listener
     * is not registered, this method does nothing.
     *
     * @param listener the RecordChangedListener
     * @see #addRecordListener
     */
    public void removeRecordListener(RecordListener listener) {
        synchronized (rsLock) {
            recordListener.removeElement(listener);
        }
    }

    /** next record's id */
    private int dbNextRecordID = 1;
    
    /** record store version */
    private int dbVersion;  

    /**
     * Returns the recordId of the next record to be added to the
     * record store. This can be useful for setting up pseudo-relational
     * relationships. That is, if you have two or more
     * record stores whose records need to refer to one another, you can
     * predetermine the recordIds of the records that will be created
     * in one record store, before populating the fields and allocating
     * the record in another record store. Note that the recordId returned
     * is only valid while the record store remains open and until a call
     * to <code>addRecord()</code>.
     *
     * @return the recordId of the next record to be added to the
     *          record store
     *
     * @exception RecordStoreNotOpenException if the record store is
     *          not open
     * @exception RecordStoreException if a different record
     *      store-related exception occurred
     */
    public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
        return dbNextRecordID;
    }

    /**
     * Adds a new record to the record store. The recordId for this
     * new record is returned. This is a blocking atomic operation.
     * The record is written to persistent storage before the
     * method returns.
     *
     * @param data the data to be stored in this record. If the record
     *      is to have zero-length data (no data), this parameter may be
     *      null.
     * @param offset the index into the data buffer of the first
     *      relevant byte for this record
     * @param numBytes the number of bytes of the data buffer to use
     *      for this record (may be zero)
     *
     * @return the recordId for the new record
     *
     * @exception RecordStoreNotOpenException if the record store is
     *      not open
     * @exception RecordStoreException if a different record
     *      store-related exception occurred
     * @exception RecordStoreFullException if the operation cannot be
     *      completed because the record store has no more room
     * @exception SecurityException if the MIDlet has read-only access
     *      to the RecordStore
     */
    public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
        synchronized (rsLock) {
            if ((data == null) && (numBytes > 0)) {
                throw new NullPointerException("illegal arguments: null " + "data,  numBytes > 0");
            }
            // get recordId for new record, update db's dbNextRecordID
            int id = dbNextRecordID++;

            /*
             * Find the offset where this record should be stored and seek to
             * that location in the file. allocateNewRecordStorage() allocates
             * the space for this record.
             */     
            RecordHeader rh = allocateNewRecordStorage(id, numBytes);
            try {
                if (data != null) {
                    rh.write(data, offset);
                }
            } catch (IOException ioe) {
                throw new RecordStoreException("error writing new record " + "data");
            }
            
            // Update the state changes to the db file.
            spNumLiveRecords++;
            dbVersion++;
            storeSPState();
            
            // Return the new record id
            return id;
        }
    }

    /**
     * Returns an enumeration for traversing a set of records in the
     * record store in an optionally specified order.<p>
     *
     * The filter, if non-null, will be used to determine what
     * subset of the record store records will be used.<p>
     *
     * The comparator, if non-null, will be used to determine the
     * order in which the records are returned.<p>
     *
     * If both the filter and comparator is null, the enumeration
     * will traverse all records in the record store in an undefined
     * order. This is the most efficient way to traverse all of the
     * records in a record store.  If a filter is used with a null
     * comparator, the enumeration will traverse the filtered records
     * in an undefined order.
     *
     * The first call to <code>RecordEnumeration.nextRecord()</code>
     * returns the record data from the first record in the sequence.
     * Subsequent calls to <code>RecordEnumeration.nextRecord()</code>
     * return the next consecutive record's data. To return the record
     * data from the previous consecutive from any
     * given point in the enumeration, call <code>previousRecord()</code>.
     * On the other hand, if after creation the first call is to
     * <code>previousRecord()</code>, the record data of the last element
     * of the enumeration will be returned. Each subsequent call to
     * <code>previousRecord()</code> will step backwards through the
     * sequence.
     *
     * @param filter if non-null, will be used to determine what
     *          subset of the record store records will be used
     * @param comparator if non-null, will be used to determine the
     *          order in which the records are returned
     * @param keepUpdated if true, the enumerator will keep its enumeration
     *          current with any changes in the records of the record
     *          store. Use with caution as there are possible
     *          performance consequences. If false the enumeration
     *          will not be kept current and may return recordIds for
     *          records that have been deleted or miss records that
     *          are added later. It may also return records out of
     *          order that have been modified after the enumeration
     *          was built. Note that any changes to records in the
     *          record store are accurately reflected when the record
     *          is later retrieved, either directly or through the
     *          enumeration. The thing that is risked by setting this
     *          parameter false is the filtering and sorting order of
     *          the enumeration when records are modified, added, or
     *          deleted.
     *
     * @exception RecordStoreNotOpenException if the record store is
     *          not open
     *
     * @see RecordEnumeration#rebuild
     *
     * @return an enumeration for traversing a set of records in the
     *          record store in an optionally specified order
     */
    public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated) throws RecordStoreNotOpenException {
        return new RecordEnumerationImpl(this, filter, comparator, keepUpdated);
    }

    /*
     * Package Private Methods
     */

    /**
     * Get the open status of this record store.  (Package accessable
     * for use by record enumeration objects.)
     *
     * @return true if record store is open, false otherwise. 
     */
    boolean isOpen() {
        if (spref == null) {
            return false;
        }
        return true;
    }

    /**
     * Returns all of the recordId's currently in the record store.
     *
     * MUST be called after obtaining rsLock, e.g in a 
     * <code>synchronized (rsLock) {</code> block.
     *
     * @return an array of the recordId's currently in the record store
     *         or null if the record store is closed.
     */
    int[] getRecordIDs() {
        if (spref == null) { // lower overhead than checkOpen()
            return null;
        }

        int index = 0;
        int[] tmp = new int[spNumLiveRecords];
        int offset = spFirstRecordOffset; // start at beginning of file
        RecordHeader rh = new RecordHeader();

        try {
            while (offset != 0) {
                rh.load(offset);
                if (rh.id > 0) {
                    tmp[index++] = rh.id;
                }
                offset = rh.nextOffset;
            }
        } catch (IOException ioe) {
            return null;
        }
        return tmp;
    }
}

/* */
