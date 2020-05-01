package com.navkar.faltu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

public class Incmining {
    int cntlarge ;
    int cntpre;
    private int upper_sup, lower_sup; //lower support threshold,upper support threshold
    public int[][] oldDB = null;//new int[100][100];
    final public List<List<Integer>> DB;
    HashMap<Object, Integer> ht = new HashMap<Object, Integer>();
    static int cnt;
    String input, output;
    int conf=0;
   ArrayList<Set<List<Integer>>> L_k = new ArrayList<Set<List<Integer>>>();//Large sequence
   ArrayList<Set<List<Integer>>> P_k = new ArrayList<Set<List<Integer>>>();//Pre-Large sequence
    ArrayList<Set<List<Integer>>> T_k = new ArrayList<Set<List<Integer>>>();
    ArrayList<Set<List<Integer>>> p_k = new ArrayList<Set<List<Integer>>>();// pre Large sequence
    ArrayList<Set<List<Integer>>> l_k = new ArrayList<Set<List<Integer>>>();//Large sequence
    protected long startTimestamp;
    protected long endTimestamp;
    long temps;

//----------------------------------------------------------------------------------
    public Incmining(String input, String output, int upper, int lower,int conf) {
        //Setup the database
        cntlarge = 0;
        cntpre = 0;
       cnt=0;


        upper_sup = upper;
        lower_sup = lower;
        this.input = input;
        this.output = output;
        this.conf=conf;
        BufferedReader br = null;
        Calendar curDate = Calendar.getInstance();
        System.out.println(curDate.getTime());
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(input));


            //NO OF RECORDS IN 'input' FILE
            while ((sCurrentLine = br.readLine()) != null) {
                cnt++;
           

            }
            MainForm.jTextArea3.append("No of transactions:" + cnt);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        curDate = Calendar.getInstance();
        System.out.println(curDate.getTime());

        DB = new ArrayList<List<Integer>>();
        splitDB(input);

        List<int[]> converter = Arrays.asList(oldDB);
        for (int[] arr : converter) {
            List<Integer> set = new ArrayList<Integer>();
            for (int i = 0; i < arr.length; i++) {
                set.add(arr[i]);
            }
            DB.add(set);
        }

        run();
    }

//--------------------------------------------------------------------------------
    public void splitDB(String input) {
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(input));
            String[] arr = null;
            int[] a = null;
            int k = 0;
            oldDB = new int[cnt][];

            while ((sCurrentLine = br.readLine()) != null) {
                arr = sCurrentLine.split(",");
                a = new int[arr.length];
                oldDB[k] = new int[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    a[i] = Integer.parseInt(arr[i]);
                    oldDB[k][i] = a[i];
                }
                k++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
//--------------------------------------------------------------------------------

    public void run() {
        startTimestamp = System.currentTimeMillis();
        

        //----------------------------- Inc large and  pre-large
        try {

            FileInputStream fis = new FileInputStream(output);
            ObjectInputStream ois = new ObjectInputStream(fis);
            L_k = (ArrayList<Set<List<Integer>>>) ois.readObject();
            P_k = (ArrayList<Set<List<Integer>>>) ois.readObject();

            ois.close();
        } catch (Exception e) {
            System.out.println("Exception during deserialization: " + e);
            System.exit(0);
        }
        //----------------------------- Inc hash table
        try {

            FileInputStream fis = new FileInputStream(output+"hash");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ht = (HashMap) ois.readObject();
            cntlarge=(int)ois.readInt();
            cntpre=(int)ois.readInt();
            
            ois.close();
        } catch (Exception e) {
            System.out.println("Exception during deserialization: " + e);
            System.exit(0);
        }


        l_k.add(findFrequent1ItemSets(DB)); //same thing as to set L_1 equal to the output
//
        for (int k = 0; !p_k.get(k).isEmpty(); k++) { //maybe k should start out as 1
            Set<List<Integer>> C_k = apriori_gen(l_k.get(k), p_k.get(k));
            Set<List<Integer>> C_kGood = new HashSet<List<Integer>>();
            Set<List<Integer>> C_kGoodPre = new HashSet<List<Integer>>();
         //   System.out.println("K="+k);
            //Find the counts in the db for each candidate in C_k
            //add the candidates with minsup to Lk,
            for (List<Integer> candidate : C_k) {
                cnt = countSupport(candidate, DB);
//-----------------------------------------------------------------------------------------------------------------HEREeeeeeeeeeeeeeeeeeee
                int val, temp = 0;
                if (ht.containsKey(candidate)) {
                    temp = ht.get(candidate);
                    ht.put(candidate, temp + cnt);

                    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    if (temp < upper_sup) // become large remove from pre-large put in large
                    {
                        if (ht.get(candidate) >= upper_sup) {
                            int i = 0;
                            for (Set<List<Integer>> set : P_k) {
                                if (i == k+1) {
                                    set.remove(candidate);
                                    break;
                                }
                                i++;
                            }
                            cntpre--;
                            cntlarge++;   //****
                            C_kGood.add(candidate);
                        } else if (ht.get(candidate) < upper_sup) {   // remove from pre-large put in Pre-large
                            int i = 0;
                            for (Set<List<Integer>> set : P_k) {
                                if (i == k+1) {
                                    set.remove(candidate);
                                    break;
                                }
                                i++;
                            }
                            C_kGoodPre.add(candidate);
                            //cntpre++;
                        }
                    } else if (temp >= upper_sup) {                 // remove from large put in large
                        if (ht.get(candidate) >= upper_sup) {
                            int i = 0;
                            for (Set<List<Integer>> set : L_k) {
                                if (i == k+1) {
                                    set.remove(candidate);
                                    break;
                                }
                                i++;
                            }
                            //cntlarge++;
                            C_kGood.add(candidate);
                        }
                    }

                } else if (cnt >= upper_sup) {
                    String str=output+"LARGE";
                    cntlarge++;
                    C_kGood.add(candidate);
                    //System.out.println("\n Upper"+candidate);
                    calculateconfidence(candidate);
                    filewrite(str,candidate,cnt);
                    ht.put(candidate, cnt);
                } else if (cnt >= lower_sup && cnt < upper_sup) {
                  String str=output+"PRELARGE";
                    cntpre++;
                    C_kGoodPre.add(candidate);
                    filewrite(str,candidate,cnt);
                    ht.put(candidate, cnt);
                }

            }

        /*
        -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ---
        if(cnt >= upper_sup) {
        C_kGood.add(candidate);
        ht.put(candidate, cnt);
        System.out.println("name:" + candidate + "  " + ht.get(candidate));

        } else if (cnt >= lower_sup && cnt < upper_sup) {
        C_kGoodPre.add(candidate);
        ht.put(candidate, cnt);
        System.out.println("name:" + candidate + "  " + ht.get(candidate));
        }
        */
        l_k.add(C_kGood);
        p_k.add(C_kGoodPre);
        }
    // for large
    for(int k=0;k<l_k.size();k++)
    {
                     Set<List<Integer>> Tk = new HashSet<List<Integer>>();
                     if(k<L_k.size())
                     {
                         Tk.addAll(L_k.get(k));
                         L_k.remove(k);
                     }
                    Tk.addAll(l_k.get(k));
                    L_k.add(k,Tk);

}  // for pre --large

      for (int k = 0;k<p_k.size();k++)
      {

                    Set<List<Integer>> Tk = new HashSet<List<Integer>>();
                    if(k<P_k.size())
                    {
                        Tk.addAll(P_k.get(k));
                        P_k.remove(k);
                    }
                        Tk.addAll(p_k.get(k));

                    P_k.add(k,Tk);

     }

    try {

            FileOutputStream fos = new FileOutputStream(output);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(L_k);
            oos.writeObject(P_k);

            //oos.writeLong(temps);
            oos.flush();
            oos.close();

     }catch (Exception e) {
            System.out.println("Exception during serialization: " + e);
        System.exit(0);
    }
    //------------------------------------------------------------------------------------
    //hash table
    try
    {

        FileOutputStream fos = new FileOutputStream(output+"hash");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(ht);

            oos.writeInt(cntlarge);
            oos.writeInt(cntpre);
        //oos.writeLong(temps);
        oos.flush();
        oos.close();

    }catch(Exception e) {
            System.out.println("Exception during serialization: " + e);
        System.exit(0);

    } //-------------------------------------------------------------------------------------------addddddd
    //Print everything in L_k
/*
    System.out.println ("Done calculating frequent LARGE itemsets");
    for(Set<List<Integer>> set : L_k){
			//Each level
			System.out.println("New Level");
           for (List<Integer> list : set) {
            System.out.print("Set: ");
            System.out.println(list);
            //cntlarge++;

        }
    }*/
/*
    System.out.println ("Done calculating frequent PRE-LARGE itemsets");
    for(Set<List<Integer>> set1 : P_k){
			//Each level
			System.out.println("New Level");
                        for (List<Integer> list : set1) {
            System.out.print("Set: ");
            System.out.println(list);
            //cntpre++;

        }

    }*/
//-----------------------------------------------------------------------------------------------------
    endTimestamp  = System.currentTimeMillis();

    MainForm.jTextArea3.append ("\n\n@=======  ALGORITHM STATISTICS===========");
    temps  = endTimestamp - startTimestamp;


    MainForm.jTextArea3.append ("\nTotal time ~ "+ temps/1000 + " s");
    MainForm.jTextArea3.append( "\nNo of Large itemsets ~ " + cntlarge);
    MainForm.jTextArea3.append("\nNo of Pre-Large itemsets ~ " + cntpre);
    MainForm.jTextArea3.append("\n@-------------------------------------------");





} //--------------------------------------------------------------------------------
        private int countSupport(List<Integer> candidate, List<List<Integer>> dB2) {
        int count = 0;

for (List<Integer> list : dB2) {
            if (list.containsAll(candidate)) {
                count++;
}
        }
        return count;

}
//--------------------------------------------------------------------------------

    private Set<List<Integer>> apriori_gen(Set<List<Integer>> Lk_1, Set<List<Integer>> Pk_1) {
        Set<List<Integer>> C_k = new HashSet<List<Integer>>();

        Set<List<Integer>> Tk = new HashSet<List<Integer>>();
        Tk.addAll(Lk_1);
        Tk.addAll(Pk_1);


for (List<Integer> i1set : Tk) {
            for (List<Integer> i2set : Tk) {
                if (oneElementLessThan(i1set, i2set)) {
                    //Create a candidate with [i1set[1..k-1], i2set[k-1]] in effect a join.
                    List<Integer> candidate = new ArrayList<Integer>();
                    candidate.addAll(i1set);
                    candidate.add(i2set.get(i2set.size() - 1));


if (containsNoInfrequentSubset(candidate, Tk)) { //line 5-7 in the book
                        C_k.add(candidate);

}

                }
            }
        }
        return C_k;




    }
    //--------------------------------------------------------------------------------
        private boolean containsNoInfrequentSubset(List<Integer> candidate,
            Set<List<Integer>> Lk_1) {
        //calculate all subsets of candidate, with lengt k-1
        Set<List<Integer>> candidate_subsets = generateSubsets(candidate);
        //check if all subsets are contained in Lk_1, return true, if not, return false.

return Lk_1.containsAll(candidate_subsets);

}
        //--------------------------------------------------------------------------------

    Set<List<Integer>> generateSubsets(List<Integer> candidate) {
        Set<List<Integer>> subsets = new HashSet<List<Integer>>();

for (int i = 0; i< candidate.size(); i++) {
            List<Integer> subset = new ArrayList<Integer>();
            subset.addAll(candidate.subList(0, i));
            subset.addAll(candidate.subList(i + 1, candidate.size()));
            subsets.add(subset);

}
        return subsets;

}
//--------------------------------------------------------------------------------

    public boolean oneElementLessThan(List<Integer> list1, List<Integer> list2) {
        for (int i = 0;i< (list1.size() - 1); i++) {
            if (list1.get(i) != list2.get(i)) {
                return false;

}
        }
        if (list1.get(list1.size() - 1) < list2.get(list1.size() - 1)) {
            return true;


        }
        else {
            return false;

      }
    }
//--------------------------------------------------------------------------------

    private Set<List<Integer>> findFrequent1ItemSets(List<List<Integer>> dB) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

for (List<Integer> list : dB) {
            for (Integer key : list) {
                if (map.containsKey(key)) {
                    map.put(key, map.get(key) + 1);


}
                else {
                    map.put(key, 1);
}
            }
        }
        //done plotting.. prune those who doesn't support the minimum threshold.
        List<Integer> keySet = new ArrayList<Integer>(map.keySet());
        // Collections.sort(keySet); //Everything in apriori has to be ascending.
        Set<List<Integer>> finalKeyset = new HashSet<List<Integer>>();
        Set<List<Integer>> finalKeysetpre = new HashSet<List<Integer>>();
        //And add each integer into it's own set.


int temp=0,cnt;
for (Integer key : keySet) {
            cnt = map.get(key);

if (ht.containsKey(key)) {
                 temp=ht.get(key);
                ht.put(key,temp + cnt);
            List<Integer> d = new ArrayList<Integer>();
            d.add(key);

if(temp <upper_sup)
            {         if (ht.get(key) >= upper_sup) {
                          for(Set<List<Integer>> set : P_k){
                              set.remove(d);
                         break;
}
                         List<Integer> goodSet = new ArrayList<Integer>();
                        goodSet.add(key);
                        finalKeyset.add(goodSet);
                        cntpre--;
                        cntlarge++;
}
                        else if(ht.get(key) < upper_sup) {
                          for(Set<List<Integer>> set : P_k){
                              set.remove(d);
                              break;
}
                         List<Integer> goodSet = new ArrayList<Integer>();
                        goodSet.add(key);
                        finalKeysetpre.add(goodSet);
                        //cntpre++;
                        }
            }
            else if(temp >= upper_sup)
            {         if (ht.get(key) >= upper_sup) {
                          for(Set<List<Integer>> set : L_k){
                              set.remove(d);
                               break;
                }
                         List<Integer> goodSet = new ArrayList<Integer>();
                        goodSet.add(key);
                        finalKeyset.add(goodSet);
                        //cntlarge++;
}
            }

            }
            else if(cnt >= upper_sup)
            {
                String str=output+"LARGE";

				List<Integer> goodSet = new ArrayList<Integer>();
				goodSet.add(key);
                                //System.out.println("\n lower"+key);
                                filewrite1(str,key,cnt);
				finalKeyset.add(goodSet);
                                ht.put(key, cnt);
                                cntlarge++;
}
             else if (cnt >= lower_sup && cnt < upper_sup) {
               
                 String str1=output+"PRELARGE";
                         List<Integer> goodSet1 = new ArrayList<Integer>();
                goodSet1.add(key);
                finalKeysetpre.add(goodSet1);
                filewrite1(str1,key,cnt);
                ht.put(key, cnt);
                cntpre++;
}

        }
         p_k.add(finalKeysetpre);
         return finalKeyset;

}
    
    
    
    private List<Integer> calculateconfidence(List<Integer> candidate) {
	
    String [] arr;
         String ss=new String();
        String x1=ss.valueOf(candidate);
        String y1=x1.replace("[","" );
        String z=y1.replace("]","" );
        System.out.println(z);
        arr=z.split("[:,:]");
       
      String status=null;
       int ct=0;
       int k=0;
         int []  val1=new int[arr.length];
        for(int i=0;i<arr.length;i++)
        {               
          val1[i]=countforconfidence(Integer.parseInt(arr[i].trim()));
      
     
        }
        if(arr.length==2)
         {
         
             double div=(val1[0]/val1[1])*100;
                  int ans=0;
                  if(div>=conf)
                  {
                     MainForm.jTextArea1.append("\n"+candidate); 
                    
                    
                  }
                  
        }
   
       
        
       
        return candidate;
}
     private int countforconfidence(Integer db) {
		BufferedReader br = null;
int count=0;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(input));
 			String[] arr1 = null;
 			
 			int k=0;
 			

			while ((sCurrentLine = br.readLine()) != null)
			{
				arr1 = sCurrentLine.split(",");
				
				
				for(int i = 0; i < arr1.length; i++)
				{
					if(Integer.parseInt( arr1[i])==db)
                                        {
                                            count++;
                                        }

				}

					k++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return count;
	} 
     
     
     
     
      private void filewrite(String out,List<Integer> xyz,int cn1)
     {
          try {            
   File file = new File(out);
             
   FileWriter fw= null;
             
   BufferedWriter bw = null; 
              
             fw  = new FileWriter(file,true);
             
              bw = new BufferedWriter(fw);
              
              List<Integer> data = xyz;
              //-------------------------------------
              String [] arr1;
              String ss=new String();
              String x12=ss.valueOf(xyz);
                                 String y12=x12.replace("[","" );
                                 String z2=y12.replace("]","" );
                                // System.out.println(z2);
                                 arr1=z2.split("[:,:]");
                                 //System.out.println(arr1);
                               
                                 bw.append("[");
                                 for(int j=0;j<arr1.length;j++)
                                 {
                                      String dataSourceName = "mining";
        String dbURL = "jdbc:odbc:" + dataSourceName;
        try { 
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        Connection con = DriverManager.getConnection(dbURL, "","");
        
        Statement st=con.createStatement();
        ResultSet rs = st.executeQuery("select * from ProductMaster where Key='"+arr1[j].trim()+"' ");
        
         if(rs.next()){
             
               bw.append(""+ rs.getString(2)+",");
                //status=status+","+rs.getString(2);
               
               
             //System.out.println(rs.getString(2));
             
         }
         
     
        rs.close();
        st.close();
        con.close();
        
        }
       catch (Exception err) {
        System.out.println( "Error: " + err );
      }
                                 }
                                 bw.append("]");
                                 bw.append("    "+cn1) ;
                                 
                                 //---------

			
              //-------------------------------------
             
            
            bw.newLine();
            bw.flush();
        
                    }
                    catch(Exception e) {
                    System.out.println("Exception during serialization: " + e);
                    System.exit(0);
                    }
     }
      
      
      
      
      private void filewrite1(String out,Integer xyz,int cn1)
     {
          try {            
   File file = new File(out);
             
   FileWriter fw= null;
             
   BufferedWriter bw = null; 
              
             fw  = new FileWriter(file,true);
             
              bw = new BufferedWriter(fw);
              
              Integer data = xyz;
              //-------------------------------------
          
                               
                                
                                      String dataSourceName = "mining";
        String dbURL = "jdbc:odbc:" + dataSourceName;
        try { 
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        Connection con = DriverManager.getConnection(dbURL, "","");
        
        Statement st=con.createStatement();
        ResultSet rs = st.executeQuery("select * from ProductMaster where Key='"+xyz+"' ");
        
         if(rs.next()){
             
               bw.append(""+ rs.getString(2)+",");
                //status=status+","+rs.getString(2);
               
               
             //System.out.println(rs.getString(2));
             
         }
         
     
        rs.close();
        st.close();
        con.close();
        
        }
       catch (Exception err) {
        System.out.println( "Error: " + err );
      }
                                 
                                 bw.append("]");
                                 bw.append("    "+cn1) ;
                                 
                                 //---------

			
              //-------------------------------------
             
            
            bw.newLine();
            bw.flush();
        
     }
                    catch(Exception e) {
                    System.out.println("Exception during serialization: " + e);
                    System.exit(0);
                    }
}
       
}
    
    

//---------------------------------------------------------------------------------

