import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.termdeposit.model.DataContainer;
import com.termdeposit.model.RandomForest;
import com.termdeposit.model.Tree;
import com.termdeposit.model.Validation;
import com.termdeposit.view.UserView;
import com.termdeposit.controller.Manager;


public class Main {

    public static void main(String[] args) {
        try{
            Manager manager = new Manager();
        }catch(Exception e){
            System.out.println(" Main: something failed.");
            e.printStackTrace(); // Prints the exception and the call stack
        }

    }
        //System.out.println(data.gettrainingDataWithMissing());
}

