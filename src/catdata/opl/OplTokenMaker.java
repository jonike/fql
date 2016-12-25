/* The following code was generated by JFlex 1.4.3 on 8/7/16 12:29 AM */

/*
 * Generated on 8/7/16 12:29 AM
 */
package catdata.opl;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


/**
 * 
 */
@SuppressWarnings({"unused", "UnusedAssignment"})
public class OplTokenMaker extends AbstractJFlexCTokenMaker {

  /** This character denotes the end of file */
  private static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  private static final int EOL_COMMENT = 4;
  private static final int YYINITIAL = 0;
  private static final int MLC = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2, 2
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\17\1\7\1\0\1\17\1\15\22\0\1\17\1\24\1\14"+
    "\1\16\1\1\1\24\1\24\1\6\1\25\1\25\1\21\1\71\1\24"+
    "\1\72\1\23\1\20\1\4\3\4\4\4\2\3\1\36\1\24\1\15"+
    "\1\74\1\73\1\24\1\16\1\45\1\5\1\46\1\41\1\47\1\5"+
    "\1\1\1\52\1\40\3\1\1\54\1\42\1\53\1\50\2\1\1\43"+
    "\1\44\1\51\5\1\1\25\1\10\1\25\1\75\1\2\1\0\1\55"+
    "\1\13\1\60\1\63\1\35\1\32\1\65\1\26\1\33\1\66\1\70"+
    "\1\34\1\62\1\12\1\61\1\30\1\67\1\57\1\31\1\27\1\11"+
    "\1\64\1\37\1\1\1\56\1\1\1\22\1\75\1\22\1\24\uff81\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\2\1\1\2\1\3\2\1\1\4\1\5\1\1"+
    "\1\6\1\7\25\1\1\6\1\10\1\11\5\10\1\12"+
    "\3\10\1\0\1\13\2\1\2\4\1\14\1\15\1\16"+
    "\23\1\1\17\1\1\1\17\12\1\1\20\15\1\1\21"+
    "\11\0\3\1\1\4\1\22\1\4\17\1\1\20\31\1"+
    "\11\0\1\1\1\4\33\1\2\0\1\23\2\0\1\24"+
    "\1\0\1\4\24\1\5\0\1\4\27\1\1\17\12\1";

  private static int [] zzUnpackAction() {
    int [] result = new int[265];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int... result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\76\0\174\0\272\0\370\0\u0136\0\272\0\u0174"+
    "\0\u01b2\0\u01f0\0\u022e\0\u026c\0\272\0\272\0\u02aa\0\u02e8"+
    "\0\u0326\0\u0364\0\u03a2\0\u03e0\0\u041e\0\u045c\0\u049a\0\u04d8"+
    "\0\u0516\0\u0554\0\u0592\0\u05d0\0\u060e\0\u064c\0\u068a\0\u06c8"+
    "\0\u0706\0\u0744\0\u0782\0\u0782\0\u07c0\0\272\0\u07fe\0\u083c"+
    "\0\u087a\0\u08b8\0\u08f6\0\272\0\u0934\0\u0972\0\u09b0\0\u09ee"+
    "\0\u0a2c\0\u0a6a\0\u0aa8\0\u0ae6\0\u0b24\0\272\0\272\0\272"+
    "\0\u0b62\0\u0ba0\0\u0bde\0\u0c1c\0\u0c5a\0\u0c98\0\u0cd6\0\u0d14"+
    "\0\u0d52\0\u0d90\0\u0dce\0\u0e0c\0\u0e4a\0\u0e88\0\u0ec6\0\u0f04"+
    "\0\u0f42\0\u0f80\0\u0fbe\0\370\0\u0ffc\0\u103a\0\u1078\0\u10b6"+
    "\0\u10f4\0\u1132\0\u1170\0\u11ae\0\u11ec\0\u122a\0\u1268\0\u12a6"+
    "\0\370\0\u12e4\0\u1322\0\u1360\0\u139e\0\u13dc\0\u141a\0\u1458"+
    "\0\u1496\0\u14d4\0\u1512\0\u1550\0\u158e\0\u15cc\0\272\0\u160a"+
    "\0\u1648\0\u1686\0\u16c4\0\u1702\0\u1740\0\u177e\0\u17bc\0\u17fa"+
    "\0\u1838\0\u1876\0\u18b4\0\u18f2\0\272\0\u1930\0\u196e\0\u19ac"+
    "\0\u19ea\0\u1a28\0\u1a66\0\u1aa4\0\u1ae2\0\u1b20\0\u1b5e\0\u1b9c"+
    "\0\u1bda\0\u1c18\0\u1c56\0\u1c94\0\u1cd2\0\u1d10\0\u1d4e\0\u1d8c"+
    "\0\u1dca\0\u1e08\0\u1e46\0\u1e84\0\u1ec2\0\u1f00\0\u1f3e\0\u1f7c"+
    "\0\u1fba\0\u1ff8\0\u2036\0\u2074\0\u20b2\0\u20f0\0\u212e\0\u216c"+
    "\0\u21aa\0\u21e8\0\u2226\0\u2264\0\u22a2\0\u22e0\0\u231e\0\u235c"+
    "\0\u239a\0\u23d8\0\u2416\0\u2454\0\u2492\0\u24d0\0\u250e\0\u254c"+
    "\0\u258a\0\u25c8\0\u2606\0\u2644\0\u2682\0\u26c0\0\u26fe\0\u273c"+
    "\0\u277a\0\u27b8\0\u27f6\0\u2834\0\u2872\0\u28b0\0\u28ee\0\u292c"+
    "\0\u296a\0\u29a8\0\u29e6\0\u2a24\0\u2a62\0\u2aa0\0\u2ade\0\u2b1c"+
    "\0\u2b5a\0\u2b98\0\u2bd6\0\u2c14\0\u2c52\0\u2c90\0\u2cce\0\u2d0c"+
    "\0\u2d4a\0\u2d88\0\u2dc6\0\u2e04\0\u2e42\0\u2e80\0\u2ebe\0\u2efc"+
    "\0\u2f3a\0\u2f78\0\u2fb6\0\u2ff4\0\u3032\0\u3070\0\u30ae\0\u30ec"+
    "\0\u312a\0\u3168\0\u31a6\0\u31e4\0\u3222\0\u3260\0\u329e\0\u32dc"+
    "\0\u331a\0\u3358\0\u2d0c\0\u3396\0\u2dc6\0\u33d4\0\u3412\0\u3450"+
    "\0\u348e\0\u34cc\0\u350a\0\u3548\0\u3586\0\u35c4\0\u3602\0\u3640"+
    "\0\u367e\0\u36bc\0\u36fa\0\u3738\0\u3776\0\u37b4\0\u37f2\0\u3830"+
    "\0\u386e\0\u38ac\0\u38ea\0\u3928\0\u3966\0\u39a4\0\u39e2\0\u3a20"+
    "\0\u3a5e\0\u3a9c\0\u3ada\0\u3b18\0\u3b56\0\u3b94\0\u3bd2\0\u3c10"+
    "\0\u3c4e";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[265];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int... result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\2\5\2\6\1\5\1\4\1\7\1\4\1\10"+
    "\1\11\1\5\1\12\2\4\1\13\1\14\1\15\1\16"+
    "\2\4\1\16\1\5\1\17\1\20\1\21\1\22\1\23"+
    "\1\5\1\24\1\15\1\25\1\26\2\5\1\27\4\5"+
    "\1\30\4\5\1\31\1\5\1\32\1\33\1\34\1\35"+
    "\1\36\1\5\1\37\1\40\1\41\1\42\1\15\1\43"+
    "\1\4\1\44\1\15\7\45\1\46\11\45\1\47\4\45"+
    "\1\50\3\45\1\51\4\45\1\52\36\45\7\53\1\54"+
    "\16\53\1\55\3\53\1\56\4\53\1\57\36\53\77\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\32\5"+
    "\5\0\3\61\2\6\1\61\2\0\4\61\2\0\1\61"+
    "\7\0\10\61\1\0\32\61\6\0\5\5\2\0\1\60"+
    "\1\5\1\62\1\5\12\0\10\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\22\5"+
    "\1\63\7\5\5\0\7\12\1\64\1\65\3\12\1\66"+
    "\61\12\17\0\1\13\76\0\1\67\1\70\55\0\5\5"+
    "\2\0\1\60\3\5\12\0\1\71\7\5\1\0\16\5"+
    "\1\72\1\73\1\74\11\5\6\0\5\5\2\0\1\60"+
    "\1\75\2\5\12\0\10\5\1\0\16\5\1\76\1\5"+
    "\1\77\11\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\5\5\1\100\1\5\1\101\1\0\16\5\1\102\1\103"+
    "\1\5\1\104\1\105\5\5\1\106\1\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\6\5\1\107\1\5\1\0"+
    "\20\5\1\110\1\5\1\111\7\5\6\0\5\5\2\0"+
    "\1\60\1\5\1\112\1\5\12\0\10\5\1\0\23\5"+
    "\1\113\1\114\5\5\6\0\5\5\2\0\1\60\1\5"+
    "\1\115\1\5\12\0\10\5\1\0\24\5\1\116\1\117"+
    "\2\5\1\120\1\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\1\121\4\5\1\122\2\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\2\5"+
    "\1\114\1\123\26\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\10\5\1\0\7\5\1\124\22\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\10\5\1\0\12\5\1\125"+
    "\17\5\6\0\5\5\2\0\1\60\1\5\1\126\1\5"+
    "\12\0\1\5\1\127\1\130\1\131\4\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\7\5\1\132"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\1\133\7\5\1\0\22\5\1\134\7\5\6\0\5\5"+
    "\2\0\1\60\2\5\1\135\12\0\2\5\1\136\5\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\16\5\1\137\3\5\1\140\7\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\7\5\1\141\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\3\5\12\0\7\5"+
    "\1\142\1\0\20\5\1\143\11\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\16\5\1\144\13\5"+
    "\6\0\5\5\2\0\1\60\1\145\2\5\12\0\10\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\7\5\1\146\1\0\32\5\100\0\1\15\2\0\7\45"+
    "\1\0\11\45\1\0\4\45\1\0\3\45\1\0\4\45"+
    "\1\0\36\45\20\0\1\147\104\0\1\150\75\0\1\151"+
    "\3\0\1\152\101\0\1\153\36\0\7\53\1\0\16\53"+
    "\1\0\3\53\1\0\4\53\1\0\36\53\27\0\1\154"+
    "\75\0\1\155\3\0\1\156\101\0\1\157\47\0\1\160"+
    "\64\0\6\61\2\0\4\61\2\0\1\61\7\0\10\61"+
    "\1\0\32\61\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\3\5\1\161\1\5\1\162\2\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\24\5"+
    "\1\163\5\5\5\0\10\64\1\164\3\64\1\165\65\64"+
    "\1\12\1\64\1\12\1\0\1\12\1\166\3\12\12\64"+
    "\1\12\2\64\1\12\24\64\1\12\16\64\1\0\5\5"+
    "\2\0\1\60\3\5\12\0\7\5\1\167\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\2\5\1\170\12\0\10\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\2\5\1\171\5\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\16\5\1\172\13\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\3\5\1\173"+
    "\4\5\1\0\32\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\1\5\1\174\6\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\7\5\1\175\1\0\16\5"+
    "\1\100\13\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\26\5\1\176\3\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\6\5\1\177\1\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\1\5\1\200"+
    "\6\5\1\0\32\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\10\5\1\0\23\5\1\201\6\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\1\202\7\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\20\5\1\203\11\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\6\5\1\114\1\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\10\5\1\0\22\5\1\204"+
    "\7\5\6\0\5\5\2\0\1\60\3\5\12\0\10\5"+
    "\1\0\22\5\1\205\7\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\20\5\1\206\11\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\1\5\1\207\1\5"+
    "\1\210\4\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\2\5\1\211\5\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\1\5\1\212\6\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\26\5\1\163\3\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\16\5\1\106\13\5"+
    "\6\0\5\5\2\0\1\60\1\213\2\5\12\0\10\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\7\5\1\214\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\1\5\1\215\6\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\4\5"+
    "\1\216\25\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\13\5\1\217\16\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\4\5\1\220\25\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\24\5\1\131\5\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\1\5\1\221\6\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\2\5\1\222\5\5\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\3\5\12\0\1\5"+
    "\1\223\6\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\16\5\1\224\13\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\6\5\1\225\1\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\3\5\1\226\4\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\1\5\1\227\6\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\2\5\1\230"+
    "\5\5\1\0\32\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\10\5\1\0\24\5\1\231\5\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\6\5\1\232\1\5\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\1\5\1\233\1\5"+
    "\12\0\10\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\16\5\1\234\13\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\25\5"+
    "\1\235\4\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\7\5\1\236\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\17\5\1\237\12\5\34\0"+
    "\1\240\76\0\1\241\101\0\1\242\100\0\1\243\65\0"+
    "\1\244\76\0\1\245\101\0\1\246\100\0\1\247\41\0"+
    "\3\250\5\0\1\250\16\0\1\250\2\0\1\250\3\0"+
    "\1\250\3\0\3\250\5\0\1\250\2\0\1\250\2\0"+
    "\1\250\13\0\5\5\2\0\1\60\3\5\12\0\10\5"+
    "\1\0\16\5\1\102\13\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\22\5\1\251\7\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\7\5\1\237\1\0"+
    "\32\5\5\0\7\64\1\0\71\64\3\252\2\64\1\164"+
    "\2\64\1\252\1\165\15\64\1\252\2\64\1\252\3\64"+
    "\1\252\3\64\3\252\5\64\1\252\2\64\1\252\2\64"+
    "\1\252\12\64\1\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\22\5\1\236\7\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\6\5\1\171\1\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\7\5\1\253"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\1\5\1\254"+
    "\1\5\12\0\10\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\1\255\7\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\1\226\7\5\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\3\5\12\0\3\5"+
    "\1\256\4\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\23\5\1\257\6\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\7\5\1\260\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\1\261\2\5\12\0"+
    "\10\5\1\0\32\5\6\0\5\5\2\0\1\60\2\5"+
    "\1\262\12\0\10\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\7\5\1\176\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\1\5\1\237\6\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\1\263\31\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\23\5\1\131\6\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\16\5"+
    "\1\264\13\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\22\5\1\131\7\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\7\5\1\265\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\22\5"+
    "\1\105\7\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\5\5\1\266\2\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\16\5\1\136\13\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\20\5\1\267\11\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\1\131\7\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\5\5\1\270\24\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\10\5\1\271\21\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\10\5\1\0\13\5\1\272\16\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\10\5\1\0\20\5\1\273"+
    "\11\5\6\0\5\5\2\0\1\60\3\5\12\0\6\5"+
    "\1\274\1\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\1\275\2\5\12\0\10\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\3\5\1\276\4\5\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\3\5\12\0\5\5"+
    "\1\277\2\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\10\5\1\300\21\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\5\5\1\301\2\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\2\5\1\302\5\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\7\5\1\106\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\1\5\1\257\6\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\7\5\1\303\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\2\5\1\304\5\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\16\5"+
    "\1\305\13\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\20\5\1\274\11\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\3\5\1\131\4\5\1\0\32\5"+
    "\35\0\1\306\103\0\1\307\74\0\1\241\63\0\1\310"+
    "\102\0\1\311\103\0\1\312\74\0\1\245\63\0\1\313"+
    "\55\0\3\314\5\0\1\314\16\0\1\314\2\0\1\314"+
    "\3\0\1\314\3\0\3\314\5\0\1\314\2\0\1\314"+
    "\2\0\1\314\13\0\5\5\2\0\1\60\1\5\1\114"+
    "\1\5\12\0\10\5\1\0\32\5\5\0\3\64\3\315"+
    "\2\64\1\164\2\64\1\315\1\165\15\64\1\315\2\64"+
    "\1\315\3\64\1\315\3\64\3\315\5\64\1\315\2\64"+
    "\1\315\2\64\1\315\12\64\1\0\5\5\2\0\1\60"+
    "\3\5\12\0\3\5\1\114\4\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\3\5\1\316\4\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\22\5\1\317\7\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\7\5\1\320\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\16\5"+
    "\1\114\13\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\21\5\1\321\10\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\20\5\1\322\11\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\22\5\1\323\7\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\7\5\1\324\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\6\5\1\325\1\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\20\5\1\321\11\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\1\5\1\326\6\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\7\5\1\131\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\6\5\1\327\23\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\10\5\1\0\15\5\1\330\14\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\10\5\1\0\14\5\1\331"+
    "\15\5\6\0\5\5\2\0\1\60\3\5\12\0\5\5"+
    "\1\332\2\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\17\5\1\114\12\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\20\5"+
    "\1\333\11\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\7\5\1\114\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\23\5\1\334\6\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\30\5"+
    "\1\335\1\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\22\5\1\336\7\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\5\5\1\337\2\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\20\5\1\340\11\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\1\114\7\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\3\5\1\341\4\5\1\0\32\5"+
    "\36\0\1\241\4\0\1\307\57\0\1\342\56\0\1\310"+
    "\1\343\3\310\1\343\2\0\3\310\2\0\1\343\1\0"+
    "\1\310\1\343\1\0\3\343\10\310\1\343\32\310\2\343"+
    "\1\0\1\343\32\0\1\245\4\0\1\312\57\0\1\344"+
    "\56\0\1\313\1\345\3\313\1\345\2\0\3\313\2\0"+
    "\1\345\1\0\1\313\1\345\1\0\3\345\10\313\1\345"+
    "\32\313\2\345\1\0\1\345\4\0\3\346\5\0\1\346"+
    "\16\0\1\346\2\0\1\346\3\0\1\346\3\0\3\346"+
    "\5\0\1\346\2\0\1\346\2\0\1\346\12\0\3\64"+
    "\3\347\2\64\1\164\2\64\1\347\1\165\15\64\1\347"+
    "\2\64\1\347\3\64\1\347\3\64\3\347\5\64\1\347"+
    "\2\64\1\347\2\64\1\347\12\64\1\0\5\5\2\0"+
    "\1\60\3\5\12\0\2\5\1\350\1\5\1\351\3\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\1\352\2\5"+
    "\12\0\10\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\1\5\1\353\1\5\12\0\10\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\1\5\1\131\6\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\16\5\1\354\13\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\6\5\1\237\1\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\20\5\1\114\11\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\6\5\1\131\1\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\5\5\1\355\2\5\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\3\5\12\0\10\5"+
    "\1\0\3\5\1\356\26\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\6\5\1\114\23\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\12\5"+
    "\1\357\17\5\6\0\5\5\2\0\1\60\2\5\1\360"+
    "\12\0\10\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\1\5\1\131\1\5\12\0\10\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\5\5\1\352\2\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\1\361\2\5"+
    "\12\0\10\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\1\5\1\237\1\5\12\0\10\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\1\5\1\362\1\5\12\0\10\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\16\5\1\363\13\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\21\5\1\364\10\5"+
    "\25\0\1\310\75\0\1\313\60\0\3\5\5\0\1\5"+
    "\16\0\1\5\2\0\1\5\3\0\1\5\3\0\3\5"+
    "\5\0\1\5\2\0\1\5\2\0\1\5\12\0\3\64"+
    "\3\12\2\64\1\164\2\64\1\12\1\165\15\64\1\12"+
    "\2\64\1\12\3\64\1\12\3\64\3\12\5\64\1\12"+
    "\2\64\1\12\2\64\1\12\12\64\1\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\20\5\1\171\11\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\22\5\1\365\7\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\1\5\1\114\6\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\1\5\1\366\6\5\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\3\5\12\0\1\5"+
    "\1\276\6\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\7\5\1\367\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\10\5\1\0\7\5\1\370"+
    "\22\5\6\0\5\5\2\0\1\60\3\5\12\0\10\5"+
    "\1\0\5\5\1\114\24\5\6\0\5\5\2\0\1\60"+
    "\1\371\2\5\12\0\10\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\10\5\1\0\16\5\1\372"+
    "\13\5\6\0\5\5\2\0\1\60\3\5\12\0\10\5"+
    "\1\0\26\5\1\114\3\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\1\5\1\373\6\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\10\5\1\0\20\5"+
    "\1\374\11\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\20\5\1\375\11\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\16\5\1\376\13\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\3\5\1\377"+
    "\4\5\1\0\32\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\10\5\1\0\10\5\1\114\21\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\1\5\1\171\6\5\1\0"+
    "\32\5\6\0\5\5\2\0\1\60\3\5\12\0\6\5"+
    "\1\u0100\1\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\10\5\1\0\22\5\1\u0101\7\5\6\0"+
    "\5\5\2\0\1\60\3\5\12\0\5\5\1\u0102\2\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\10\5\1\0\23\5\1\114\6\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\1\5\1\u0103\6\5\1\0\32\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\10\5\1\0"+
    "\6\5\1\u0104\23\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\5\5\1\u0105\2\5\1\0\32\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\10\5\1\0\20\5\1\237"+
    "\11\5\6\0\5\5\2\0\1\60\3\5\12\0\2\5"+
    "\1\352\5\5\1\0\32\5\6\0\5\5\2\0\1\60"+
    "\3\5\12\0\5\5\1\162\2\5\1\0\32\5\6\0"+
    "\5\5\2\0\1\60\1\5\1\u0106\1\5\12\0\10\5"+
    "\1\0\32\5\6\0\5\5\2\0\1\60\3\5\12\0"+
    "\1\5\1\u0107\6\5\1\0\32\5\6\0\5\5\2\0"+
    "\1\60\3\5\12\0\10\5\1\0\24\5\1\u0108\5\5"+
    "\6\0\5\5\2\0\1\60\3\5\12\0\5\5\1\163"+
    "\2\5\1\0\32\5\6\0\5\5\2\0\1\60\3\5"+
    "\12\0\10\5\1\0\6\5\1\u0109\23\5\6\0\5\5"+
    "\2\0\1\60\3\5\12\0\1\5\1\127\6\5\1\0"+
    "\32\5\5\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[15500];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int... result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\11\2\1\1\11\5\1\2\11\27\1\1\11"+
    "\5\1\1\11\3\1\1\0\5\1\3\11\56\1\1\11"+
    "\11\0\4\1\1\11\52\1\11\0\35\1\2\0\1\1"+
    "\2\0\1\1\1\0\25\1\5\0\43\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[265];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int... result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /* user code: */


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public OplTokenMaker() {
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addToken(int, int, int)
	 */
	private void addHyperlinkToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, true);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int tokenType) {
		addToken(zzStartRead, zzMarkedPos-1, tokenType);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addHyperlinkToken(int, int, int)
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, false);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *        occurs.
	 * @param hyperlink Whether this token is a hyperlink.
	 */
	@Override
	public void addToken(char[] array, int start, int end, int tokenType,
						int startOffset, boolean hyperlink) {
		super.addToken(array, start,end, tokenType, startOffset, hyperlink);
		zzStartRead = zzMarkedPos;
	}


	/**
	 * Returns the text to place at the beginning and end of a
	 * line to "comment" it in a this programming language.
	 *
	 * @return The start and end strings to add to a line to "comment"
	 *         it out.
	 */
	public static String[] getLineCommentStartAndEnd() {
		return new String[] { "//", null };
	}


	/**
	 * Returns the first token in the linked list of tokens generated
	 * from <code>text</code>.  This method must be implemented by
	 * subclasses so they can correctly implement syntax highlighting.
	 *
	 * @param text The text from which to get tokens.
	 * @param initialTokenType The token type we should start with.
	 * @param startOffset The offset into the document at which
	 *        <code>text</code> starts.
	 * @return The first <code>Token</code> in a linked list representing
	 *         the syntax highlighted text.
	 */
	@Override
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

		resetTokenList();
        offsetShift = -text.offset + startOffset;

		// Start off in the proper state.
		int state = TokenTypes.NULL;
		switch (initialTokenType) {
						case TokenTypes.COMMENT_MULTILINE:
				state = MLC;
				start = text.offset;
				break;

			/* No documentation comments */
			default:
				state = TokenTypes.NULL;
		}

		s = text;

        yyreset(zzReader);
        yybegin(state);
        return yylex();


	}


	/**
	 * Refills the input buffer.
	 *
	 * @return      <code>true</code> if EOF was reached, otherwise
	 *              <code>false</code>.
	 */
	private boolean zzRefill() {
		return zzCurrentPos>=s.offset+s.count;
	}


	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>YY_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
    private void yyreset(Reader reader) {
		// 's' has been updated.
		zzBuffer = s.array;
		/*
		 * We replaced the line below with the two below it because zzRefill
		 * no longer "refills" the buffer (since the way we do it, it's always
		 * "full" the first time through, since it points to the segment's
		 * array).  So, we assign zzEndRead here.
		 */
		//zzStartRead = zzEndRead = s.offset;
		zzStartRead = s.offset;
		zzEndRead = zzStartRead + s.count - 1;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
		zzLexicalState = YYINITIAL;
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
	}




  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  private OplTokenMaker(Reader in) {
      zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public OplTokenMaker(InputStream in) {
    this(new InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 180) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Closes the input stream.
   */
  public final void yyclose() throws IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  @Override
public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  private int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private static void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  @SuppressWarnings("ConstantConditions")
  private Token yylex() {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 3:
            addNullToken();
            return firstToken;
          case 21: break;
        case 14:
            start = zzMarkedPos-2;
            yybegin(MLC);
          case 22: break;
        case 5:
            addToken(TokenTypes.WHITESPACE);
          case 23: break;
        case 18:
            addToken(TokenTypes.ERROR_STRING_DOUBLE);
          case 24: break;
        case 15:
            addToken(TokenTypes.RESERVED_WORD);
          case 25: break;
        case 7:
            addToken(TokenTypes.SEPARATOR);
          case 26: break;
        case 1:
            addToken(TokenTypes.IDENTIFIER);
          case 27: break;
        case 10:
            addToken(start,zzStartRead-1, TokenTypes.COMMENT_EOL);
            addNullToken();
            return firstToken;
          case 28: break;
        case 13:
            start = zzMarkedPos-2;
            yybegin(EOL_COMMENT);
          case 29: break;
        case 4:
            addToken(TokenTypes.ERROR_STRING_DOUBLE);
            addNullToken();
            return firstToken;
          case 30: break;
        case 17:
            yybegin(YYINITIAL);
            addToken(start,zzStartRead+2-1, TokenTypes.COMMENT_MULTILINE);
          case 31: break;
        case 12:
            addToken(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE);
          case 32: break;
        case 20: 
          { int temp=zzStartRead; addToken(start,zzStartRead-1, TokenTypes.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, TokenTypes.COMMENT_EOL); start = zzMarkedPos;
          }
        case 33: break;
        case 19:
            int temp=zzStartRead;
            addToken(start,zzStartRead-1, TokenTypes.COMMENT_MULTILINE);
            addHyperlinkToken(temp,zzMarkedPos-1, TokenTypes.COMMENT_MULTILINE);
            start = zzMarkedPos;
          case 34: break;
        case 16:
            addToken(TokenTypes.RESERVED_WORD_2);
          case 35: break;
        case 11:
            addToken(TokenTypes.ERROR_NUMBER_FORMAT);
          case 36: break;
        case 2:
            addToken(TokenTypes.LITERAL_NUMBER_DECIMAL_INT);
          case 37: break;
        case 6:
            addToken(TokenTypes.OPERATOR);
          case 38: break;
        case 8:
          case 39: break;
        case 9:
            addToken(start,zzStartRead-1, TokenTypes.COMMENT_MULTILINE);
            return firstToken;
          case 40: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            switch (zzLexicalState) {
            case EOL_COMMENT:
                addToken(start,zzStartRead-1, TokenTypes.COMMENT_EOL);
                addNullToken();
                return firstToken;
                case 266: break;
            case YYINITIAL:
                addNullToken();
                return firstToken;
                case 267: break;
            case MLC:
                addToken(start,zzStartRead-1, TokenTypes.COMMENT_MULTILINE);
                return firstToken;
                case 268: break;
            default:
            return null;
            }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
