package com.navkar.billGeneratorUI;

import javax.swing.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloWorld {
		HelloWorld(){  
		ApplicationContext context = new ClassPathXmlApplicationContext("/spring.xml");
		Message message= (Message) context.getBean("helloWorld");  
	    message.getMessage();
			JFrame f=new JFrame(message.getMessage()); 
			f.setTitle(message.getMessage());
			JButton b=new JButton(message.getMessage());  
			b.setBounds(130,100,100, 40);  
			f.add(b);  
			f.setSize(300,400);  
			f.setLayout(null);  
			f.setVisible(true);  
			f.setTitle(message.getMessage());
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
    }  

	public static void main(String[] args) {  
	    new HelloWorld();  
	}
	
}
