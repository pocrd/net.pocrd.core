package net.pocrd.util;

import net.pocrd.entity.CallerInfo;

public class TokenHelper {
    private AesHelper aes;
    public  TokenHelper(String pwd) {
        aes = new AesHelper(pwd);
    }
    
    public CallerInfo parse(String token){
        return null;
    }
}
