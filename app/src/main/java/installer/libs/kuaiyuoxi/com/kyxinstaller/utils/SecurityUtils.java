package installer.libs.kuaiyuoxi.com.kyxinstaller.utils;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

/**
 * Created by xujiaoyong on 15/6/19.
 */
public class SecurityUtils {

    public static byte[] decrypt( byte[] cSrc )
    {
        int i, h, l, m, n;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for( i = 0; i < cSrc.length; i = i + 2 )
        {
            h = ( cSrc[ i ] - 'x' );
            l = ( cSrc[ i + 1 ] - 'z' );
            m = ( h << 4 );
            n = ( l & 0xf );
            out.write( m + n );
        }
        return out.toByteArray();
    }

    public static String getMd5( String value )
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance( "MD5" );
            md5.update( value.getBytes() );
            return toHexString( md5.digest() );
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMd5( String value, String charset )
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance( "MD5" );
            md5.update( value.getBytes( charset ) );
            return toHexString( md5.digest() );
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }
        return "";
    }

    private static final char	HEX_DIGITS[]	= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String toHexString( byte[] b )
    {
        StringBuilder sb = new StringBuilder( b.length * 2 );
        for( int i = 0; i < b.length; i++ )
        {
            sb.append( HEX_DIGITS[ ( b[ i ] & 0xf0 ) >>> 4 ] );
            sb.append( HEX_DIGITS[ b[ i ] & 0x0f ] );
        }
        return sb.toString();
    }

    public static byte[] encrypt( byte[] cSrc )
    {
        byte c;
        int i, h, l;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for( i = 0; i < cSrc.length; i++ )
        {
            c = cSrc[ i ];
            h = ( c >> 4 ) & 0xf;
            l = c & 0xf;
            out.write( h + 'x' );
            out.write( l + 'z' );
        }
        return out.toByteArray();
    }


    public static String decodeResponse( String value ) throws Exception
    {
        try
        {
            byte[] data = Base.decode( value );
            byte[] resultData = decrypt( data );
            return new String( resultData );
        }
        catch( Exception e )
        {
            throw e;
        }
    }

    protected static char	hexDigits[]	= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };




    private static String bufferToHex( byte bytes[] )
    {
        return bufferToHex( bytes, 0, bytes.length );
    }

    private static String bufferToHex( byte bytes[], int m, int n )
    {
        StringBuffer stringbuffer = new StringBuffer( 2 * n );
        int k = m + n;
        for( int l = m; l < k; l++ )
        {
            appendHexPair( bytes[ l ], stringbuffer );
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair( byte bt, StringBuffer stringbuffer )
    {
        char c0 = hexDigits[ ( bt & 0xf0 ) >> 4 ];
        char c1 = hexDigits[ bt & 0xf ];
        stringbuffer.append( c0 );
        stringbuffer.append( c1 );
    }

}
