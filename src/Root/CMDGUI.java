package Root;

import java.util.Scanner;

public class CMDGUI {
    private ControllerInterface[] controllers;
    private String location = null;
    private MenuItem[] controllerMenuItems;
    private ConsoleColors console;

    public CMDGUI(ControllerInterface[] controllers){
        this.controllers = controllers;
        controllerMenuItems = new MenuItem[controllers.length];
        for(int i = 0;i<controllers.length;i++){
            controllerMenuItems[i] = controllers[i].getController();
        }
        console = new ConsoleColors();
        getInput();
    }

    public void getInput(){

        boolean b = true;
        Scanner scan = new Scanner(System.in);
        MenuItem[] printMenuItems = null;
        String input = null;
        String[] printPreText;
        Form printForm;
        ControllerInterface currentcontroller;
        while(b){
            if(input != null){
                try{
                    int i = Integer.parseInt(input);
                    location = printMenuItems[i-1].getLocation();

                }catch (Exception e){
                    console.printTxtRed("Ukendt kommando");
                    console.print(true);
                    console.clearTxtBuffer();
                }
            }
            //Nulstiller alt
            printMenuItems=null;
            printPreText = null;
            printForm = null;
            currentcontroller = null;

            if(location== null || location.equals("/")){
                printMenuItems = controllerMenuItems;
            }
            else {
                for(int i = 0;i<controllers.length && printMenuItems == null;i++){
                    if(controllers[i].getPage(location) != null){
                        //Breaks up the page
                        printPreText = controllers[i].getPage(location).getText();
                        printForm = controllers[i].getPage(location).getForm();
                        printMenuItems = controllers[i].getPage(location).getMenuItems();
                        currentcontroller = controllers[i];
                    }

                }
            }
            //Prints the preText
            if(printPreText != null){
                for(int i = 0; i < printPreText.length; i++){
                    console.printTxtYellow(printPreText[i]);
                    console.print(true);
                    console.clearTxtBuffer();
                }
            }
            //Prints the form and returns data to target location
            if(printForm != null){
                for(int i = 0; i < printForm.getQuestions().length; i++){
                    //Gets the question
                    console.printTxtBlue(printForm.getQuestions()[i].getQuestion());
                    console.print(true);
                    console.clearTxtBuffer();
                    //Sets the answer
                    printForm.getQuestions()[i].setAnswer(scan.nextLine());
                }
                String[] returnstr = currentcontroller.postForm(printForm);
                for(int i = 0; i < returnstr.length; i++){
                    console.printTxtYellow(returnstr[i]);
                    console.print(true);
                    console.clearTxtBuffer();
                }
            }
            //Prints the menu
            if(printMenuItems == null){
                console.printTxtRed("Stien findes ikke");
                console.print(true);
                console.clearTxtBuffer();
            }
            else{
                console.printTxtBlue("Valgmuligheder");
                console.print(true);
                console.clearTxtBuffer();
                for(int i = 0;i<printMenuItems.length;i++){
                    console.printTxtGreen((i+1) + ": "+ printMenuItems[i].getName());
                    console.print(true);
                    console.clearTxtBuffer();
                }
            }
            console.printTxtRed("0: Exit");
            console.print(true);
            console.clearTxtBuffer();
            input = scan.nextLine();
            if(input.equals("0")){
                b = false;
            }
        }

    }
}
