/**
 *  Copyright (c) 2014 http://www.lushapp.wang
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.lushapp.common.utils.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 如果需要身份认证，则创建一个密码验证器
 * @author honey.zhao@aliyun.com  
 * @date   2014-5-5 下午3:39:45
 */
public class MyAuthenticator extends Authenticator{  
    String userName=null;
    String password=null;
    
    public MyAuthenticator(){  
    }
    
    public MyAuthenticator(String username, String password) {   
        this.userName = username;   
        this.password = password;   
    }  
    
    protected PasswordAuthentication getPasswordAuthentication(){  
        return new PasswordAuthentication(userName, password);  
    }  
}