package com.example.Kill_Deathinator;

import android.app.Activity;
import android.os.Bundle;
import java.io.*;
import java.util.*;

public class MyActivity extends Activity{
    /**
     * Called when the activity is first created.
     */
    @Override
        public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
    }
}
/*public static void readFile(String fileName,  playerScore [] scores)
      {
         try
         {
            Scanner input = new Scanner(new FileReader(fileName));
            int i=0;									//index for placement in the array
            long damage=0;
            String name="";
            int time=0;
            while (input.hasNextLine() && i < scores.length)		//while there is another line in the file
            {
               try
               {
                  String sentence = input.nextLine();
                  String [] parts = sentence.split(" ");

                  damage=Long.parseLong(decode(parts[0]));		//propertyDamage (score)
                  name = decode(parts[1]);							//monster name
                  time = Integer.parseInt(decode(parts[2]));	//time expired
               }
                  catch (java.util.InputMismatchException ex1)			//file is corrupted or doesn't exist - clear high scores and remake the file
                  {
                     for(int j=0; j < scores.length; j++)
                        scores[j] = new playerScore(0, "none", 0);
                     writeToFile(scores, fileName);
                     return;
                  }
                  catch (java.util.NoSuchElementException ex2)			//file is corrupted or doesn't exist - clear high scores and remake the file
                  {
                     for(int j=0; j < scores.length; j++)
                        scores[j] = new playerScore(0, "none", 0);
                     writeToFile(scores, fileName);
                     return;
                  }
               scores[i++]= new playerScore(damage, name, time);			//add the line into the array
            }
            input.close();
         }
            catch (IOException ex3)			//file is corrupted or doesn't exist - clear high scores and remake the file
            {
               for(int i=0; i < scores.length; i++)
                  scores[i] = new playerScore(0, "none", 0);
               writeToFile(scores, fileName);
            }
      }
*/
