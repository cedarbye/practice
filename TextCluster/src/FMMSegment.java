import java.lang.*;
import java.io.*;
import java.util.*;

public class FMMSegment {
  Dictionary dic;
  int totleNumber; //��¼�����ܹ��ʻ���
  HashMap<String, Float> vocabulary; //��¼���ı��л�ȡ�����Ĵ�

  public FMMSegment() {
  }

  public FMMSegment(Dictionary newDic) {
    dic = newDic;
    totleNumber = 0;
    vocabulary = new HashMap<String, Float>();
  }

  public int wordSegment(String Sentence) { //���ķִ�
    int senLen = Sentence.length();
    int i = 0, j = 0;
    int M = 12;
    String word;
    boolean bFind = false;
    FileAppender fa=new FileAppender("vocabulary.txt");

      while (i < senLen) {
        int N = i + M < senLen ? i + M : senLen + 1;
        bFind = false;
        for (j = N - 1; j > i; j--) {
          word = Sentence.substring(i, j);
          if (dic.Find(word)) {
            if (j > i + 1) {
              if (!vocabulary.containsKey(word)) {
                vocabulary.put(word, new Float(0));
                totleNumber++; //�ۼ��ܴʻ���
                //����ȡ�ĵ���д���ļ�
                fa.append(word);
                //System.out.print(word + " ");
              }
            }
            bFind = true;
            i = j;
            break;
          }
        }
        if (bFind == false) {
          i = j + 1;
        }
      }
    return 1;
  }


  public void fileSegment(String fileName) { //���ж���
    try {
      BufferedReader in = new BufferedReader(
          new FileReader(fileName));
      String s;
      while ( (s = in.readLine()) != null) {
        wordSegment(s);
      }
    }
    catch (IOException e) {
      System.out.println(e);
    }
  }

  public int NumOfVoc() {
    return totleNumber;
  }
}
