package Root;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Date;
import java.io.IOException;
import java.io.PrintWriter;

public class  WebStatus extends Thread{
    private volatile FIFO<WheelInterface> resqueue;
    private volatile WheelInterface[] wheels;
    ServerSocket serverConnect;
    private boolean debug = false;
    Threadhandler pool;
    public WebStatus(FIFO<WheelInterface> resqueue, WheelInterface[] wheels, Threadhandler pool) {
        this(resqueue, wheels, pool, false);
    }

    public WebStatus(FIFO<WheelInterface> resqueue, WheelInterface[] wheels, Threadhandler pool, boolean debug){
        this.resqueue = resqueue;
        this.wheels = wheels;
        this.debug = debug;
        this.pool = pool;
        //We listen on port 80 (normal webserver port)
        try
        {
            this.serverConnect = new ServerSocket(80);
        }
        catch (IOException e)
        {
            if(debug) System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    public void run(){
        // we listen until user halts server execution
        while (!isInterrupted()) {
            //When we get a new connection we create a new Socket to talk to the client. and create an object of the type MyServerSocket
            try
            {
                WebServer myServer = new WebServer(serverConnect.accept(), resqueue, wheels, pool, debug);
                if(debug) System.out.println("Connecton opened. (" + new Date() + ")");
                // create a dedicated thread to manage the client connection
                Thread thread = new Thread(myServer);
                thread.start();
            }
            catch (IOException e)
            {
                if(debug) System.err.println("Server Connection error : " + e.getMessage());
            }

        }
        if(debug) System.out.println("webstatus has closed down!");
    }

    public void close(){
        interrupt();
        try
        {
            serverConnect.close();
        }
        catch (IOException e)
        {
            if(debug) System.err.println("Server Connection error : " + e.getMessage());
        }
    }

}

class WebServer implements Runnable
{
    private Socket client;
    private volatile FIFO<WheelInterface> resqueue;
    private volatile WheelInterface[] wheels;
    private boolean debug = false;
    Threadhandler pool = null;

    public WebServer(Socket c, FIFO<WheelInterface> resqueue, WheelInterface[] wheels, Threadhandler pool)
    {
        this(c, resqueue, wheels, pool, false);
    }

    public WebServer(Socket c, FIFO<WheelInterface> resqueue, WheelInterface[] wheels, Threadhandler pool, boolean debug)
    {
        client = c;
        this.resqueue = resqueue;
        this.wheels = wheels;
        this.debug = debug;
        this.pool = pool;
    }

    public void run()
    {
        BufferedReader in = null;
        PrintWriter out = null;
        try
        {
            String data = null;
            String clientAddress = client.getInetAddress().getHostAddress();
            if(debug) System.out.println("\r\nNew connection from " + clientAddress);

            Form form = null;


            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
            String input = in.readLine();

            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();
            String fileRequested = parse.nextToken().toLowerCase();
            //Our file request we send to the controller to get data back from it
            if(debug) System.out.println("FileRequest: " + fileRequested);
            if (method.equals("GET"))
            {
                out = new PrintWriter(client.getOutputStream());
                BeltInterface[] belts = pool.getBelts();

                String page = "";
                if(fileRequested.equals("/rest")){
                    if(debug) System.out.println("Supported connection, returning rest page");

                    page += "{\n";

                    page += "\t\"queue\":\n\t{\n";
                    page += "\t\t\"items\": " + resqueue.size() + ",\n";
                    if(resqueue.size() > 0) {

                        page += "\t\t\"wheeltypes\":\n\t\t {\n";
                        for(int i = 0; i < wheels.length; i++){
                            if(i+1 == wheels.length){
                                page += "\t\t\t\"" + wheels[i].getName().replace(" ", "") + "\": \"" + wheels[i].getName() + "\"\n";
                            }
                            else{
                                page += "\t\t\t\"" + wheels[i].getName().replace(" ", "") + "\": \"" + wheels[i].getName() + "\",\n";
                            }
                        }
                        page += "\t\t},\n";
                        page += "\t\t\"wheelcount\":\n\t\t {\n";
                        int[] wheelcount = countWheeltypesAmount();
                        for(int i = 0; i < wheelcount.length; i++){
                            if(i+1 == wheelcount.length){
                                page += "\t\t\t\"" + wheels[i].getName().replace(" ", "") + "\": " + wheelcount[i] + "\n";
                            }
                            else{
                                page += "\t\t\t\"" + wheels[i].getName().replace(" ", "") + "\": " + wheelcount[i] + ",\n";
                            }
                        }
                        page += "\t\t},\n";
                        page += "\t\t\"nextitems\": \"";
                        for (int i = 0; i < Math.min(5, resqueue.size()); i++) {
                            page += resqueue.get(i).getName();
                            if (i + 1 != Math.min(5, resqueue.size())) {
                                page += ", ";
                            }
                        }
                        page += "\",\n";
                    }


                    page += "\t\t\"beltprogress\":\n\t\t{\n";
                    for(int i = 0; i < belts.length; i++){
                        page += "\t\t\t\"" + belts[i].getName().replace(" ", "")+"\":\"" + getBeltProgress(belts[i]) + "\"";
                        if(i + 1 != belts.length){
                            page += ",\n";
                        }
                        else{
                            page += "\n";
                        }
                    }
                    page += "\t\t},\n";

                    page += "\t\t\"beltstate\":\n\t\t{\n";
                    for(int i = 0; i < belts.length; i++){
                        String progress = getBeltProgress(belts[i]);
                        page += "\t\t\t\"" + belts[i].getName().replace(" ", "")+"\":\"" + getBeltState(belts[i]) + "\"";
                        if(i + 1 != belts.length){
                            page += ",\n";
                        }
                        else{
                            page += "\n";
                        }
                    }
                    page += "\t\t}\n";

                    page += "\t}\n";

                    page += "}\n";
                }
                else{
                    if(debug) System.out.println("Supported connection, returning statistics page");

                    page = getHTMLHeader("Carwheel production status");
                    page += "<h1>Production line</h1>";
                    if(debug) System.out.println("Supported connection, returning statistics page");

                    page += "<div>";
                    for(int i = 0; i < belts.length; i++){
                        page += "<div style='display: inline-block; border: 1px solid black; margin: 20px; width: 200px;'>"+
                                "<div style='width: 100%; border-bottom: 1px solid black; background: silver;'>Name: "+belts[i].getName()+"</div>" +
                                "<div style='width: 100%; border-bottom: 1px solid black; background: lightgrey' id='"+belts[i].getName().replace(" ", "")+"-state'>Loading..</div>" +
                                "<div style='height: 150px; text-align: center; padding: 50px;' id='"+belts[i].getName().replace(" ", "")+"-progress'>Loading..</div>" +
                                "</div>";
                    }
                    page += "</div>";
                    page += "<h1>Production Queue</h1>";
                    page += "<p>Wheels in queue <span id=\"items\">"+resqueue.size()+"</span></p>";
                    String queueinfo_display = "none";
                    if(resqueue.size() > 0){
                        queueinfo_display = "block";
                    }
                    page += "<div id=\"queueinfo\" style='display: "+queueinfo_display+"'>";
                    int[] wheelcount = countWheeltypesAmount();
                    page += "<table>";
                    for(int i = 0; i < wheelcount.length; i++){
                        page += "<tr><td>" + wheels[i].getName() + "</td><td><span id=\""+wheels[i].getName().replace(" ", "")+"\">" + wheelcount[i] + "</span></td></tr>";
                    }
                    page += "</table>";

                    page += "<p>Next few items are <span id=\"nextitems\">";
                    for (int i = 0; i < Math.min(5, resqueue.size()); i++) {
                        page += resqueue.get(i).getName();
                        if (i + 1 != Math.min(5, resqueue.size())) {
                            page += ", ";
                        }
                    }
                    page += "</span>.";
                    page += "</div>";

                    page += getHTMLFooter();
                }

                outData(out, page);
            }

            if(debug) System.out.println(input);

        }
        catch(Exception e)
        {
            if(debug) System.err.println("Server error : " + e);
        }
        finally
        {
            try
            {
                in.close();
                out.close();
                client.close();
            }
            catch(Exception e)
            {
                if(debug) System.err.println("Error closing stream : " + e.getMessage());
            }
        }


    }

    private String getBeltState(BeltInterface belt){
        String state = "";
        if(belt.getState() == BeltState.WAITING) state = "Waiting";
        if(belt.getState() == BeltState.CLEANING) state = "Cleaning";
        if(belt.getState() == BeltState.PREPARING) state = "Preparing";
        if(belt.getState() == BeltState.RUNNING) state = "Running";
        if(belt.getState() == BeltState.INTERRUPTED) state = "Interrupted";
        return state;
    }

    private String getBeltProgress(BeltInterface belt){
        String progress = "";
        if(belt.getState() == BeltState.WAITING) progress = "0";
        if(belt.getState() == BeltState.CLEANING) progress = "0";
        if(belt.getState() == BeltState.INTERRUPTED) progress = "0";
        if(belt.getState() == BeltState.PREPARING) progress = ""+ Math.round(100 * ((2000 - (float)belt.getRemainingTime()) / (float)2000));
        if(belt.getState() == BeltState.RUNNING) progress = ""+ Math.round(((((float)belt.getWheel().getProductionTime())-(float)belt.getRemainingTime())/((float)belt.getWheel().getProductionTime())) * 100);
        return progress;
    }

    //Counts how many of each wheel type there is in the queue
    private int[] countWheeltypesAmount(){
        int[] wheelcount = new int[wheels.length];
        for(int i = 0; i < wheelcount.length; i++){
            wheelcount[i] = 0;
        }
        for(int i = 0; i < resqueue.size(); i++){
            WheelInterface w = resqueue.get(i);
            for(int j = 0; j < wheels.length; j++){
                if(w.getName().equals(wheels[j].getName())){
                    wheelcount[j]++;
                }
            }
        }
        return wheelcount;
    }

    private void outHeader(PrintWriter out, int length)
    {
        out.println("HTTP/1.1 200 OK");
        out.println("Server: Java HTTP Server from SSaurel : 1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: text/html; charset=utf-8");
        out.println("Content-length: " + length);
        out.println(); // blank line between headers and content, very important !
        out.flush(); // flush character output stream buffer
    }

    private void outData(PrintWriter out, String s)
    {
        outHeader(out, s.length());
        out.println(s);
        out.flush();
    }

    private String getHTMLHeader(String title)
    {
        return("<!DOCTYPE html>" +
                "<html xmlns='http://www.w3.org/1999/xhtml'>" +
                "<head>" +
                "<title>" + title + "</title>" +
                " <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>" +
                "<meta charset='UTF-8' />" +
                "</head>" +
                "<body>");
    }

    private String getHTMLFooter()
    {
        return("<script> setInterval(function(){refreshData();}, 250);" +
                "function refreshData(){ $.ajax({ " +
                    "method: \"GET\"," +
                    "url: \"rest\", " +
                    "contentType: \"JSON\" " +
            "}).done(function(data) { " +
                "var jsondata = JSON.parse(data);" +
                "for(var k in jsondata['queue']['beltprogress']){" +
                    "var myid = '#' + k + '-progress';" +
                    "var progress = jsondata['queue']['beltprogress'][k]; " +
                    "var missingprogress = 100 - progress; " +
                    "if(jsondata['queue']['beltprogress'][k] == 0){" +
                        "$(myid).html('');" +
                    "} else {" +
                        "$(myid).html(jsondata['queue']['beltprogress'][k] + '%');" +
                    "}" +
                    "if(jsondata['queue']['beltstate'][k] == 'Interrupted'){" +
                        "$(myid).css({'background':'linear-gradient(to bottom, #ff0000 0%, #ff0000 100%)'});" +
                    "}else { " +
                        "$(myid).css({'background':'linear-gradient(to bottom, #ffffff '+missingprogress+'%, #00ff00 '+progress+'%)'});" +
                    "}" +
                "}" +
                "for(var k in jsondata['queue']['beltstate']){" +
                    "var myid = '#' + k + '-state';" +
                    "$(myid).html('Status: ' + jsondata['queue']['beltstate'][k]);" +
                "}" +
                "$('#items').html(jsondata['queue']['items']);" +
                "if(jsondata['queue']['items'] > 0){" +
                    "for(var k in jsondata['queue']['wheelcount']){" +
                        "var myid = '#' + k;" +
                        "$(myid).html(jsondata['queue']['wheelcount'][k]);" +
                    "}" +
                "$('#nextitems').html(jsondata['queue']['nextitems']);" +
                "$('#queueinfo').show();" +
                "}" +
                "else{ $('#queueinfo').hide(); }" +
    "}); }" +
                "</script></body></html>");
    }
}