// ���ķִʴʵ���
// Class for Dictionary
//
import java.util.*;
import java.io.*;
import java.lang.*;

public class Dictionary
{
        HashMap<String, Integer> hm;		//a word set

        public Dictionary()
        {
                hm = new HashMap<String, Integer>();
        }

        public Dictionary(String fileName)
        {
                hm = new HashMap<String, Integer>();
                Load(fileName);
        }

        public void Load(String fileName)    //װ�غ����ֵ�
        {
                try
                {
                        BufferedReader in=
                                new BufferedReader(
                                        new FileReader(fileName) );

                        String s;
                        String []words;
                        while((s = in.readLine()) != null)
                        {
                                words = s.split("\t");
                                hm.put(words[0],new Integer(0));
                        }
                }
                catch(IOException e)
                {
                        System.out.println("Error: " + e);
                }
        }

        public boolean Find(String word)    //���ֵ����ѯ��
        {
                return hm.containsKey(word);
        }


}
