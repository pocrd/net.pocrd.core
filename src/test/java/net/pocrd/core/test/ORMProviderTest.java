package net.pocrd.core.test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import net.pocrd.core.test.model.ORMObject;
import net.pocrd.util.ORMProvider;

import org.junit.Assert;
import org.junit.Test;

import com.sun.rowset.CachedRowSetImpl;

public class ORMProviderTest {
    @Test
    public void testORMProvider() throws SQLException, UnsupportedEncodingException {
        byte[] bs = "abcdefghijklmnopqrstuvwxyz".getBytes("utf-8");
        CachedRowSetImpl crs = new CachedRowSetImpl();

        RowSetMetaDataImpl rsmdi = new RowSetMetaDataImpl();
        rsmdi.setColumnCount(24);
        rsmdi.setColumnType(1, Types.BOOLEAN);
        rsmdi.setColumnType(2, Types.BIGINT);
        rsmdi.setColumnType(3, Types.BLOB);
        rsmdi.setNullable(3, ResultSetMetaData.columnNullable);
        rsmdi.setColumnType(4, Types.BINARY);
        rsmdi.setColumnType(5, Types.SMALLINT);
        rsmdi.setColumnType(6, Types.CLOB);
        rsmdi.setNullable(6, ResultSetMetaData.columnNullable);
        rsmdi.setColumnType(7, Types.DOUBLE);
        rsmdi.setColumnType(8, Types.FLOAT);
        rsmdi.setColumnType(9, Types.INTEGER);
        rsmdi.setColumnType(10, Types.BIGINT);
        rsmdi.setColumnType(11, Types.VARCHAR);
        rsmdi.setColumnType(12, Types.SMALLINT);
        rsmdi.setColumnType(13, Types.BOOLEAN);
        rsmdi.setColumnType(14, Types.DECIMAL);
        rsmdi.setColumnType(15, Types.BLOB);
        rsmdi.setNullable(15, ResultSetMetaData.columnNullable);
        rsmdi.setColumnType(16, Types.BINARY);
        rsmdi.setColumnType(17, Types.SMALLINT);
        rsmdi.setColumnType(18, Types.CLOB);
        rsmdi.setNullable(18, ResultSetMetaData.columnNullable);
        rsmdi.setColumnType(19, Types.DOUBLE);
        rsmdi.setColumnType(20, Types.FLOAT);
        rsmdi.setColumnType(21, Types.INTEGER);
        rsmdi.setColumnType(22, Types.BIGINT);
        rsmdi.setColumnType(23, Types.VARCHAR);
        rsmdi.setColumnType(24, Types.SMALLINT);
        crs.setMetaData(rsmdi);

        ORMObject obj = new ORMObject();
        obj.b1 = true;
        obj.bd1 = new BigDecimal("1234567890987654321");
        obj.bl1 = new SerialBlob(bs);
        obj.bs1 = bs;
        obj.by1 = (byte)0x11;
        obj.cl1 = new SerialClob("abcdefghijklmnopqrstuvwxyz".toCharArray());
        obj.d1 = 1.23456;
        obj.f1 = 2.345f;
        obj.i1 = 1234567;
        obj.l1 = 98765432109876543L;
        obj.s1 = "abcxyz";
        obj.sh1 = 28;
        obj.setB(true);
        obj.setBd(new BigDecimal("12345678909876543210"));
        obj.setBl(new SerialBlob(bs));
        obj.setBs(bs);
        obj.setBy((byte)0x12);
        obj.setCl(new SerialClob("abcdefghijklmnopqrstuvwxyz".toCharArray()));
        obj.setD(1.2345678);
        obj.setF(2.345432f);
        obj.setI(123456);
        obj.setL(98765433210987L);
        obj.setS("xyzabc123");
        obj.setSh((short)124);

        crs.last();
        for (int i = 0; i < 3; i++) {
            crs.moveToInsertRow();
            crs.updateBoolean(1, obj.b1);
            crs.updateBigDecimal(2, obj.bd1);
            // crs.updateBlob(3, obj.bl1);
            crs.updateBytes(4, obj.bs1);
            crs.updateByte(5, obj.by1);
            // crs.updateClob(6, obj.cl1);
            crs.updateDouble(7, obj.d1);
            crs.updateFloat(8, obj.f1);
            crs.updateInt(9, obj.i1);
            crs.updateLong(10, obj.l1);
            crs.updateString(11, obj.s1);
            crs.updateShort(12, obj.sh1);
            crs.updateBoolean(13, obj.getB());
            crs.updateBigDecimal(14, obj.getBd());
            // crs.updateBlob(15, obj.getBl());
            crs.updateBytes(16, obj.getBs());
            crs.updateByte(17, obj.getBy());
            // crs.updateClob(18, obj.getCl());
            crs.updateDouble(19, obj.getD());
            crs.updateFloat(20, obj.getF());
            crs.updateInt(21, obj.getI());
            crs.updateLong(22, obj.getL());
            crs.updateString(23, obj.getS());
            crs.updateShort(24, obj.getSh());
            crs.insertRow();
        }
        crs.moveToCurrentRow();
        crs.beforeFirst();
        List<ORMObject> list = ORMProvider.getList(
                "select b1, bd1, bl1, bs1, by1, cl1, d1, f1, i1, l1, s1, sh1, b, bd, bl, bs, by, cl, d, f, i, l, s, sh from xyz", crs,
                ORMObject.class);
        Assert.assertTrue(list.size() == 3);
        for (ORMObject o : list) {
            Assert.assertEquals(obj.d1, o.d1, 0);
            Assert.assertEquals(obj.f1, o.f1, 0);
            Assert.assertEquals(obj.l1, o.l1);
            Assert.assertEquals(obj.s1, o.s1);
            Assert.assertEquals(obj.b1, o.b1);
            Assert.assertEquals(obj.bd1, o.bd1);
            //Assert.assertEquals(obj.bl1, o.bl1);
            Assert.assertEquals(obj.bs1, o.bs1);
            Assert.assertEquals(obj.by1, o.by1);
            //Assert.assertEquals(obj.cl1, o.cl1);
            Assert.assertEquals(obj.i1, o.i1);
            Assert.assertEquals(obj.sh1, o.sh1);
            
            Assert.assertEquals(obj.getD(), o.getD(), 0);
            Assert.assertEquals(obj.getF(), o.getF(), 0);
            Assert.assertEquals(obj.getL(), o.getL());
            Assert.assertEquals(obj.getS(), o.getS());
            Assert.assertEquals(obj.getB(), o.getB());
            Assert.assertEquals(obj.getBd(), o.getBd());
            //Assert.assertEquals(obj.getBl(), o.getBl());
            Assert.assertEquals(obj.getBs(), o.getBs());
            Assert.assertEquals(obj.getBy(), o.getBy());
            //Assert.assertEquals(obj.getCl(), o.getCl());
            Assert.assertEquals(obj.getI(), o.getI());
            Assert.assertEquals(obj.getSh(), o.getSh());
        }
    }
}
