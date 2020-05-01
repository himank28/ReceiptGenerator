package com.navkar.billGeneratorUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloWorld {
	JFrame frame;
		HelloWorld(){  
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		
		Message message= (Message) context.getBean("helloWorld");  
	    message.getMessage();
			 frame = new JFrame(message.getMessage()); 
		     
			 final JLabel label = new JLabel();            
		     label.setBounds(20,150, 200,50);
		     
		     final JPasswordField value = new JPasswordField();   
		     value.setBounds(100,75,100,30);   
		     
		     JLabel l1=new JLabel("Username:");    
		     l1.setBounds(20,20, 80,30);    
		     
		     JLabel l2=new JLabel("Password:");    
		     l2.setBounds(20,75, 80,30);    
		     
		     JButton b = new JButton("Login");  
		     b.setBounds(100,120, 80,30);    
		     
		     final JTextField text = new JTextField();  
		     text.setBounds(100,20, 100,30);    
		     
		     frame .add(value); frame .add(l1); frame .add(label); frame .add(l2); frame .add(b); frame .add(text);  
		     frame .setSize(500,300);    
		     frame .setLayout(null);    
		     frame .setVisible(true);     
		     b.addActionListener(new ActionListener() {  
		     public void actionPerformed(ActionEvent e) {    
		    	 char [] password = value.getPassword();
		    	 String pass="";
		    	 for(char a:password){
		    		 pass+=a;
			     };
			     if(pass.equals("12")){
			    	 frame.setVisible(false);
			    	 ReceiptWindowUI receiptWindow = new ReceiptWindowUI();
			    }
			     String data = "User " + text.getText();  
			     data += ", Pass: "   
			     + new String(pass);
			     
			     label.setText(data);          
			                }  
		             });   

			frame .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
    }  
public static void main(String[] args) {  
    new HelloWorld();  
}
	
}
