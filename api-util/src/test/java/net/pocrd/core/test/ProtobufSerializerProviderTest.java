package net.pocrd.core.test;

public class ProtobufSerializerProviderTest {
    //
    //    @Test
    //    public void testXml() {
    //        Test_Obj.Builder builder = Test_Obj.newBuilder();
    //        builder.setB(true);
    //        builder.addBs(true);
    //        builder.addBs(false);
    //        builder.addBs(true);
    //        builder.addBs(false);
    //
    //        builder.setD(1.234567D);
    //        builder.addDs(1.23D);
    //        builder.addDs(1.234D);
    //        builder.addDs(1.2345D);
    //
    //        builder.setF(1.234567F);
    //        builder.addFs(1.23F);
    //        builder.addFs(1.234F);
    //        builder.addFs(1.2345F);
    //
    //        builder.setI(1);
    //        builder.addIs(2);
    //        builder.addIs(3);
    //        builder.addIs(4);
    //
    //        builder.setL(123456789L);
    //        builder.addLs(123456789L);
    //        builder.addLs(1234567890L);
    //        builder.addLs(9876543210L);
    //
    //        builder.setS("hello");
    //        builder.addSs("hello");
    //        builder.addSs("<![CDATA[<xml>]]>");
    //        builder.addSs("!");
    //
    //        Test_String.Builder ts = Test_String.newBuilder();
    //        ts.setStr("hello world!");
    //        builder.setSo(ts.build());
    //        builder.addSos(ts.build());
    //        builder.addSos(ts.build());
    //        builder.addSos(ts.build());
    //
    //        builder.setAnother(builder.build());
    //        builder.addAs(Test_Obj.getDefaultInstance());
    //        builder.addAs(builder.build());
    //
    //        Test_Obj obj = builder.build();
    //
    //        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
    //        ProtobufSerializerProvider.getSerializer(Test_Obj.class).toXml(obj, out1, true);
    //        System.out.println(new String(out1.toByteArray(), ConstField.UTF8));
    //
    //        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
    //        ProtobufSerializerProvider.getSerializer(Test_Obj.class).toJson(obj, out2, true);
    //        System.out.println(new String(out2.toByteArray(), ConstField.UTF8));
    //
    //        ByteArrayOutputStream out3 = new ByteArrayOutputStream();
    //        try {
    //            Test_Obj obj1 = builder.build();
    //            obj1.writeTo(out3);
    //            Test_Obj obj2 = Test_Obj.newBuilder().mergeFrom(new ByteArrayInputStream(out3.toByteArray())).build();
    //            assertEquals(obj1, obj2);
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //            fail();
    //        }
    //    }
}
