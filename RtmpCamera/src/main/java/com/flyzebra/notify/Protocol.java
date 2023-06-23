package com.flyzebra.notify;

/**
 * @hide ClassName: Protocol
 * Author FlyZebra
 * 2021/10/27 0027 15:32
 * Describ:
 **/
public class Protocol {
    //u->s
    //user->server heartbeat
    //2byte header EEAA
    //2byte 0001
    //4byte data length
    //8byte timesnamp
    public final static byte US_HEARTBEAT[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_US_HEARTBEAT = 0x0100;

    //u->s
    //server->user heartbeat
    //2byte header EEAA
    //2byte 0002
    //4byte data length
    //8byte timesnamp
    public final static byte SU_HEARTBEAT[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SU_HEARTBEAT = 0x0200;

    //u->s
    //terminal->server heartbeat
    //2byte header EEAA
    //2byte 0003
    //4byte data length
    //8byte timesnamp
    public final static byte TS_HEARTBEAT[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_TS_HEARTBEAT = 0x0300;

    //u->s
    //server->terminal heartbeat
    //2byte header EEAA
    //2byte 0004
    //4byte data length
    //8byte timesnamp
    public final static byte ST_HEARTBEAT[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_ST_HEARTBEAT = 0x0400;

    //u->t
    //user->terminal heartbeat
    //2byte header EEAA
    //2byte 0005
    //4byte data length
    //8byte TID
    //8byte UID
    //8byte timesnamp
    public final static byte UT_HEARTBEAT[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_UT_HEARTBEAT = 0x0500;

    //t->u
    //user->terminal heartbeat
    //2byte header EEAA
    //2byte 0006
    //4byte data length
    //8byte TID
    //8byte UID
    //8byte timesnamp
    public final static byte TU_HEARTBEAT[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_TU_HEARTBEAT = 0x0600;

    //u-->u t-->t
    //terminal is connect
    //2byte header EEAA
    //2byte 0103
    //4byte data length
    //8byte TID
    public final static byte T_CONNECTED[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_T_CONNECTED = 0x0301;

    //u-->u t-->t
    //terminal is disconnect
    //2byte header EEAA
    //2byte 0104
    //4byte data length
    //8byte TID
    public final static byte T_DISCONNECTED[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_T_DISCONNECTED = 0x0401;

    //u-->u t-->t
    //remote is connect
    //2byte header EEAA
    //2byte 0105
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte U_CONNECTED[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_U_CONNECTED = 0x0501;

    //u-->u t-->t
    //remote is disconnect
    //2byte header EEAA
    //2byte 0106
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte U_DISCONNECTED[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_U_DISCONNECTED = 0x0601;

    //t-->u
    //terminal login media server
    //2byte header EEAA
    //2byte 0107
    //4byte data length
    //8byte TID
    //2byte screen width
    //2byte screen height
    //2byte camera width
    //2byte camera height
    //40byte name
    public final static byte T_LOGIN[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x38,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_T_LOGIN = 0x0701;

    //t-->u
    //terminal info
    //2byte header EEAA
    //2byte 0108
    //4byte data length
    //8byte TID
    //48byte json
    public final static byte T_INFO[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x38,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_T_INFO = 0x0801;

    //u-->t
    //user login media server
    //2byte header EEAA
    //2byte 0109
    //4byte data length
    //8byte UID
    //48byte other info
    public final static byte U_LOGIN[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x38,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_U_LOGIN = 0x0901;

    //t-->u
    //terminal info
    //2byte header EEAA
    //2byte 010A
    //4byte data length
    //8byte UID
    //48byte json
    public final static byte U_INFO[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x0A, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x38,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_U_INFO = 0x0A01;

    //u-->s
    //user add tid
    //2byte header EEAA
    //2byte 010B
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte USER_TID_ADD[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x0B, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_USER_TID_ADD = 0x0B01;

    //u-->s
    //user remove tid
    //2byte header EEAA
    //2byte 010C
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte USER_TID_REMOVE[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x01, (byte) 0x0C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_USER_TID_REMOVE = 0x0C01;

    //-----------------SCREEN------------------
    //u-->t
    //start recv screen data
    //2byte header EEAA
    //2byte 0201
    //4byte data length
    //8byte TID
    //8byte UID
    //2byte dpi capture width, default is 640P
    //1byte format 0-default h264 1 hevc
    //1byte level 1-16 default 8
    //1byte fps 1-60 default 16
    public final static byte SCREEN_U_READY[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x80, (byte) 0x00, (byte) 0x08, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SCREEN_U_READY = 0x0102;

    //t-->u
    //screen encoder start
    //2byte header EEAA
    //2byte 0202
    //4byte data length
    //8byte TID
    //2byte width
    //2byte height
    //2byte format 00-h264, 01-hevc
    public final static byte SCREEN_T_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SCREEN_T_START = 0x0202;

    //u-->t
    //start decoder start
    //2byte header EEAA
    //2byte 0203
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte SCREEN_U_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SCREEN_U_START = 0x0302;

    //u-->t
    //user stop screen
    //2byte header EEAA
    //2byte 0204
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte SCREEN_U_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SCREEN_U_STOP = 0x0402;

    //t-->u
    //teminal stop screen
    //2byte header EEAA
    //2byte 0205
    //4byte data length
    //8byte TID
    public final static byte SCREEN_T_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SCREEN_T_STOP = 0x0502;

    //t-->u
    //screen avc data
    //2byte header EEAA
    //2byte 0206
    //4byte data length
    //8byte TID
    //8byte Time Stamp
    public final static byte SCREEN_AVC[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SCREEN_AVC = 0x0602;

    //t-->t
    //screen yuv data
    //2byte header EEAA
    //2byte 0207
    //4byte data length
    //8byte TID
    //2byte width
    //2byte height
    //2byte format
    //2byte num
    public final static byte SCREEN_YUV[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SCREEN_YUV = 0x0702;

    //-----------------SNDOUT-------------------
    //u-->t
    //start play sound
    //2byte header EEAA
    //2byte 0211
    //4byte data length
    //8byte TID
    //8byte UID
    //2byte sample_rate
    //2byte changle
    //2byte format
    public final static byte SNDOUT_U_READY[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SNDOUT_U_READY = 0x1102;

    //t-->u
    //sound encoder start
    //2byte header EEAA
    //2byte 0212
    //4byte data length
    //8byte TID
    //2byte sample_rate
    //2byte changle
    //2byte format
    public final static byte SNDOUT_T_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SNDOUT_T_START = 0x1202;

    //u-->t
    //start decoder start
    //2byte header EEAA
    //2byte 0213
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte SNDOUT_U_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SNDOUT_U_START = 0x1302;

    //u-->t
    //stop sound
    //2byte header EEAA
    //2byte 0214
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte SNDOUT_U_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SNDOUT_U_STOP = 0x1402;

    //t-->u
    //stop sound
    //2byte header EEAA
    //2byte 0215
    //4byte data length
    //8byte TID
    public final static byte SNDOUT_T_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x15, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SNDOUT_T_STOP = 0x1502;

    //t-->u
    //sound aac data
    //2byte header EEAA
    //2byte 0216
    //4byte data length
    //8byte TID
    //8byte Time Stamp
    public final static byte SNDOUT_AAC[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x16, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SNDOUT_AAC = 0x1602;

    //t-->t
    //sound pcm data
    //2byte  header EEAA
    //2byte  0217
    //4byte  data length
    //8byte  TID
    //2byte  sample
    //2byte  channel
    //2byte  format
    //2byte  num
    public final static byte SNDOUT_PCM[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x17, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SNDOUT_PCM = 0x1702;

    //-----------------CAMOUT------------------
    //u-->t
    //start recv camout data
    //2byte header EEAA
    //2byte 0221
    //4byte data length
    //8byte TID
    //8byte UID
    //2byte width
    //2byte height
    //1byte format 0-default h264 1 hevc
    //1byte level 1-10 default 8
    //1byte fps 1-30 default 24
    public final static byte CAMOUT_U_READY[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0xD0, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x18, (byte) 0x00
    };
    public final static int TYPE_CAMOUT_U_READY = 0x2102;

    //t-->u
    //start camout encoder
    //2byte header EEAA
    //2byte 0222
    //4byte data length
    //8byte TID
    //2byte width
    //2byte height
    //2byte format 00-h264, 01-hevc
    //2byte level
    public final static byte CAMOUT_T_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x22, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMOUT_T_START = 0x2202;

    //t-->u
    //start camout decoder
    //2byte header EEAA
    //2byte 0223
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte CAMOUT_U_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x23, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMOUT_U_START = 0x2302;

    //u-->t
    //stop camout
    //2byte header EEAA
    //2byte 0224
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte CAMOUT_U_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x24, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMOUT_U_STOP = 0x2402;

    //t-->u
    //stop camout
    //2byte header EEAA
    //2byte 0225
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte CAMOUT_T_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x25, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMOUT_T_STOP = 0x2502;

    //t-->u
    //camera avc data
    //2byte header EEAA
    //2byte 0226
    //4byte data length
    //8byte TID
    //8byte Time Stamp
    public final static byte CAMOUT_AVC[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x26, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMOUT_AVC = 0x2602;

    //t-->t
    //camera yuv data
    //2byte header EEAA
    //2byte 0227
    //4byte data length
    //8byte TID
    //2byte width
    //2byte height
    //2byte format
    //2byte num
    public final static byte CAMOUT_YUV[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x27, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMOUT_YUV = 0x2702;

    //-----------------MICOUT------------------
    //u-->t
    //start recv micout data
    //2byte header EEAA
    //2byte 0231
    //4byte data length
    //8byte TID
    //8byte UID
    //2byte sample
    //2byte channel
    //2byte format
    public final static byte MICOUT_U_READY[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x31, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICOUT_U_READY = 0x3102;

    //t-->u
    //start micout encoder
    //2byte header EEAA
    //2byte 0232
    //4byte data length
    //8byte TID
    //2byte sample
    //2byte channel
    //2byte format
    public final static byte MICOUT_T_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICOUT_T_START = 0x3202;

    //t-->u
    //start micout decoder
    //2byte header EEAA
    //2byte 0233
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte MICOUT_U_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x33, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICOUT_U_START = 0x3302;

    //u-->t
    //stop micout
    //2byte header EEAA
    //2byte 0234
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte MICOUT_U_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICOUT_U_STOP = 0x3402;

    //t-->u
    //stop micout
    //2byte header EEAA
    //2byte 0235
    //4byte data length
    //8byte TID
    public final static byte MICOUT_T_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x35, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICOUT_T_STOP = 0x3502;

    //t-->u
    //micout aac data
    //2byte header EEAA
    //2byte 0236
    //4byte data length
    //8byte TID
    //8byte Time Stamp
    public final static byte MICOUT_AAC[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x36, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICOUT_AAC = 0x3602;

    //t-->t
    //micout pcm data
    //2byte  header EEAA
    //2byte  0237
    //4byte  data length
    //8byte  TID
    //2byte  sample
    //2byte  channel
    //2byte  format
    //2byte  num
    public final static byte MICOUT_PCM[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x37, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICOUT_PCM = 0x3702;

    //-----------------CAMFIX------------------
    //u-->t
    //start send camfix data
    //2byte header EEAA
    //2byte 0241
    //4byte data length
    //8byte TID
    //8byte UID
    //2byte width
    //2byte height
    //1byte format 0-default h264 1 hevc
    //1byte level 1-10 default 8
    //1byte fps 1-30 default 24
    public final static byte CAMFIX_U_READY[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x41, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0xD0, (byte) 0x05, (byte) 0xA0, (byte) 0x00, (byte) 0x08, (byte) 0x18, (byte) 0x00
    };
    public final static int TYPE_CAMFIX_U_READY = 0x4102;

    //t-->u
    //start camfix decoer
    //2byte header EEAA
    //2byte 0242
    //4byte data length
    //8byte TID
    //2byte width
    //2byte height
    //2byte format 00-h264, 01-hevc
    //2byte level
    public final static byte CAMFIX_T_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMFIX_T_START = 0x4202;

    //t-->u
    //start camfix encoder
    //2byte header EEAA
    //2byte 0243
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte CAMFIX_U_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x43, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMFIX_U_START = 0x4302;

    //u-->t
    //stop camfix
    //2byte header EEAA
    //2byte 0244
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte CAMFIX_U_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x44, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMFIX_U_STOP = 0x4402;

    //t-->u
    //stop camfix
    //2byte header EEAA
    //2byte 0245
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte CAMFIX_T_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMFIX_T_STOP = 0x4502;

    //u-->t
    //camfix avc data
    //2byte header EEAA
    //2byte 0246
    //4byte data length
    //8byte UID
    //8byte Time Stamp
    public final static byte CAMFIX_AVC[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x46, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMFIX_AVC = 0x4602;

    //u-->u
    //camfix yuv data
    //2byte header EEAA
    //2byte 0247
    //4byte data length
    //8byte UID
    //2byte width
    //2byte height
    //2byte format
    //2byte num
    public final static byte CAMFIX_YUV[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x47, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMFIX_YUV = 0x4702;

    //t-->u
    //camera opened
    //2byte header EEAA
    //2byte 0248
    //4byte data length
    //8byte TID
    //2byte width
    //2byte height
    //2byte format
    //2byte camera id 0x0000-front or 0x0001-back
    public final static byte CAMERA_OPEN[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x48, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMERA_OPEN = 0x4802;

    //t-->u
    //close camera
    //2byte header EEAA
    //2byte 0249
    //4byte data length
    //8byte TID
    public final static byte CAMERA_CLOSE[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x49, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_CAMERA_CLOSE = 0x4902;

    //-----------------MICFIX------------------
    //u-->t
    //start send micfix data
    //2byte header EEAA
    //2byte 0251
    //4byte data length
    //8byte TID
    //8byte UID
    //2byte sample
    //2byte channel
    //2byte format
    public final static byte MICFIX_U_READY[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x51, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICFIX_U_READY = 0x5102;

    //t-->u
    //start micfix decoder
    //2byte header EEAA
    //2byte 0252
    //4byte data length
    //8byte TID
    //2byte sample
    //2byte channel
    //2byte format
    public final static byte MICFIX_T_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x52, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICFIX_T_START = 0x5202;

    //t-->u
    //start micfix encoder
    //2byte header EEAA
    //2byte 0253
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte MICFIX_U_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x53, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICFIX_U_START = 0x5302;

    //u-->t
    //micfix stop
    //2byte header EEAA
    //2byte 0254
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte MICFIX_U_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICFIX_U_STOP = 0x5402;

    //t-->u
    //micfix stop
    //2byte header EEAA
    //2byte 0255
    //4byte data length
    //8byte TID
    public final static byte MICFIX_T_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICFIX_T_STOP = 0x5502;

    //u-->t
    //mic fixed aac data
    //2byte header EEAA
    //2byte 0256
    //4byte data length
    //8byte UID
    //8byte Time Stamp
    public final static byte MICFIX_AAC[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x56, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICFIX_AAC = 0x5602;

    //u-->u
    //mic pcm data
    //2byte  header EEAA
    //2byte  0257
    //4byte  data length
    //8byte  UID
    //2byte  sample
    //2byte  channel
    //2byte  format
    //2byte  num
    public final static byte MICFIX_PCM[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x57, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MICFIX_PCM = 0x5702;

    //t-->u
    //mic opened
    //2byte header EEAA
    //2byte 0258
    //4byte data length
    //8byte TID
    //2byte sample
    //2byte channel
    //2byte format
    public final static byte MIC_OPEN[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x58, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MIC_OPEN = 0x5802;

    //t-->u
    //mic closed
    //2byte header EEAA
    //2byte 0259
    //4byte data length
    //8byte TID
    public final static byte MIC_CLOSE[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x02, (byte) 0x59, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MIC_CLOSE = 0x5902;

    //-----------------INPUT------------------
    // u-->t
    //input touch single
    //2byte header EEAA
    //2byte 0301
    //4byte data length
    //8byte TID
    //2byte action
    //1byte point count
    //1byte screen orientation
    //2byte width
    //2byte heigh
    //8byte downTime
    //2byte x...
    //2byte y...
    public final static byte INPUT_TOUCH_SINGLE[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x38,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_TOUCH_SINGLE = 0x0103;

    //u-->t
    //input key single
    //2byte header EEAA
    //2byte 0302
    //4byte data length
    //8byte TID
    //2byte down&up 0x00, down 0x01, up 0x02
    //2byte key_vaule
    public final static byte INPUT_KEY_SINGLE[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_KEY_SINGLE = 0x0203;

    //u-->t
    //input text single
    //2byte header EEAA
    //2byte 0303
    //4byte data length
    //8byte TID
    public final static byte INPUT_TEXT_SINGLE[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_TEXT_SINGLE = 0x0303;

    // u-->t
    //input touch multi
    //2byte header EEAA
    //2byte 0311
    //4byte data length
    //8byte UID
    //2byte action
    //1byte point count
    //1byte screen orientation
    //2byte width
    //2byte heigh
    //8byte downTime
    //2byte x...
    //2byte y...
    public final static byte INPUT_TOUCH_MULTI[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x38,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_TOUCH_MULTI = 0x1103;

    //u-->t
    //input key multi
    //2byte header EEAA
    //2byte 0312
    //4byte data length
    //8byte UID
    //2byte down&up 0x00, down 0x01, up 0x02
    //2byte key_vaule
    public final static byte INPUT_KEY_MULTI[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_KEY_MULTI = 0x1203;

    //u-->t
    //input text multi
    //2byte header EEAA
    //2byte 0313
    //4byte data length
    //8byte TID
    public final static byte INPUT_TEXT_MULTI[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_TEXT_MULTI = 0x1303;

    //u-->s
    //start mutil input
    //2byte header EEAA
    //2byte 0321
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte INPUT_MULTI_U_START[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_MULTI_U_START = 0x2103;

    //s-->t
    //notify start mutil input
    //2byte header EEAA
    //2byte 0322
    //4byte data length
    //8byte TID
    public final static byte INPUT_MULTI_S_READY[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x22, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    };
    public final static int TYPE_INPUT_MULTI_S_READY = 0x2203;

    //u-->s
    //stop mutil input
    //2byte header EEAA
    //2byte 0323
    //4byte data length
    //8byte TID
    //8byte UID
    public final static byte INPUT_MULTI_U_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x23, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_MULTI_U_STOP = 0x2303;

    //s-->u
    //notify stop mutil input
    //2byte header EEAA
    //2byte 0324
    //4byte data length
    //8byte TID
    public final static byte INPUT_MULTI_S_STOP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x03, (byte) 0x24, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_INPUT_MULTI_S_STOP = 0x2403;

    //-----------------OTHER------------------

    //u-->t
    //restart mctl
    //2byte header EEAA
    //2byte 0403
    //4byte data length
    //8byte TID
    public final static byte MCTL_REBOOT[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x04, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_MCTL_REBOOT = 0x0304;

    //u-->t
    //system reboot
    //2byte header EEAA
    //2byte 0404
    //4byte data length
    //8byte TID
    public final static byte SYSTEM_REBOOT[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_SYSTEM_REBOOT = 0x0404;

    //u-->t
    //system reboot
    //2byte header EEAA
    //2byte 0405
    //4byte data length
    //8byte TID
    //char * cmd
    public final static byte RUN_APP[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x04, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_RUN_APP = 0x0504;

    //u-->t
    //system reboot
    //2byte header EEAA
    //2byte 0406
    //4byte data length
    //8byte TID
    //char * cmd
    public final static byte EXEC_SHELL[] = {
            (byte) 0xEE, (byte) 0xAA, (byte) 0x04, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
    public final static int TYPE_EXEC_SHELL = 0x0604;
}
