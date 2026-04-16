package com.dany.falcon;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import com.dany.falcon.gui.MainApp;
import com.dany.falcon.ia.AIProvider;
import com.dany.falcon.ia.AIService;
import com.dany.falcon.ia.AIServiceFactory;
import com.dany.falcon.chat.ChatService;

import java.util.List;


import java.util.Scanner;

/**
 *
 * @author bytrayed
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("iniciando desde Main Principal");
        MainApp.launch(MainApp.class, args);
    }
    private static void old (){

        Scanner sc =new Scanner(System.in);

        int intents =0;
        System.out.println("Iniciando Chat con IA");

        AIService ai = AIServiceFactory.create(AIProvider.GEMINI);
        ChatService chat = new ChatService(ai);
        List<String> comandosSalida = List.of("exit", "salir", "quit","q");


        while(true){
            System.out.println("pregunta a la IA: ");
            String entrada= sc.nextLine();

            if(comandosSalida.contains(entrada.toLowerCase())){
                System.out.println("bye bye");
                break;
            }
            System.out.println("Esperando respuesta de la IA...");

            String res = chat.sendMessage(entrada);
            System.out.println(res);

            intents ++;
        }

    }

}
