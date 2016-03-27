package Source;


import java.io.*;
import java.util.*;



/**
 * @author cedar
 * @version 1.0
 * @since 12/9/2015
 * @category: ������
 *            structSV()  �����ַ�����״̬��
 *            structAB(double delta)  ����״̬ת�ƾ������ַ���ϣ��ͬʱʵ��add deltaƽ��
 *            Viterbi()   ����������ɷ�ģ������viterbi�㷨ʵ�ִ����ע
 *            score()    ��עЧ������
 */
public class struct_S_V {
	State_set sts = new State_set();        //״̬��
	Symbol_set sys=new Symbol_set();        //�ַ���
	List<String> state_table=new ArrayList<String>();  //�������д��ԣ����ڼ���״̬ת���������
	int TOTAL_NUM=0;            //���д���
	int STATE_NUM=0;            //���д�����
	int A_STATE[][];           //״̬ת��������
	double A_table[][];         //״̬ת�����ʱ�
	double Un_Reg[];          //δ��½���ض�״̬�ĳ��ָ���
	Map B_map =new HashMap();  //�ʱ�
	
	int total_words=0;      //����ע����
	int correct_words=0;    //��ע��ȷ��
	double correct_rate=0.0; //��ȷ��
	
	
	File infile = new File ("199801.txt");     //�����ļ�
	File outfile1 = new File ("ts.txt");       //״̬���ļ�
	File outfile2 = new File ("ty.txt");       //�ַ����ļ�
	File outfile3 = new File ("b_table.txt");  //�ַ�������  b���ļ�
	File outfile6 =new File ("a_table.txt");  //  ״̬ת�ƾ��� a���ļ�
	File outfile4 = new File ("test.txt");    //  �ӹ��õĿ������ڲ��Ե��ĵ�
	File infile2 = new File ("test_S.txt");         // �������ڲ��Ա�עЧ���Ĳ����ĵ�
	File outfile5 = new File ("result.txt");       // ��ע���
	
	//------------------------------------------------------------------------------
	//�����ַ�����״̬��  Ԥ�ϵ�ǰ�ڼӹ�
	public void structSV()throws IOException{
		
		FileReader in = new FileReader(infile);//�����ļ�
		FileWriter out1 =new FileWriter(outfile1);//״̬���ļ�
		FileWriter out2 = new FileWriter(outfile2);//�ַ����ļ�
		FileWriter out4 = new FileWriter(outfile4);//  �ӹ��õĿ������ڲ��Ե��ĵ�
		State temp1=new State("/v");          // Ԥ�Ȳ���һ�����ڵĴ��ԣ������жϴ�����
		temp1.num=0;
		sts.state_set.add(temp1);
		state_table.add("/v");
		
		int ch;
		String element="";    //��+���� ��
		String end="";       // ����
		String symbol="";     // ��
		int tar=1;           //�����Ƿ��Ѿ���������еı�־
		while ((ch = in.read()) != -1){                     //���ļ�
			if ((char)(ch)=='\r'||(char)(ch)=='\n'){
				out2.write(ch);
				out4.write(ch);
				continue;
			}
			if ((char)ch==' '||(char)(ch)=='['||(char)(ch)==']'){ //���� ��������
				continue;
			}
			
			element=element+ (char)ch;
			while((char) (ch = in.read())!=' '&&(char)(ch)!=']'&&(char)(ch)!='\n'){ //����һ������
				element=element+(char)ch;
				}
				if ((element.length()==21&&element.endsWith("/m"))||element.length()==2||element.length()==1){
					element="";               //���������ڵĻ�����
					continue;
				}
				else{
					int index=element.lastIndexOf("/");      //������صĴʺʹ��Բ���
					if (index<element.length()&&index!=0){
						end=element.substring(element.lastIndexOf("/"), element.length()); 
						symbol=element.substring(0,element.lastIndexOf("/"));
					}
					
					tar=1;
					
						for (int i=0;i<sts.state_set.size();i++){  // �жϴ����Ƿ��Ѿ������ڱ���
							if(sts.state_set.get(i).state.equals(end)){  //����
								sts.state_set.get(i).num++;			//�ô��Գ��ִ���++					
								out2.write(element+" ");
								out4.write(symbol+" ");
								element="";
								symbol="";
								end="";
								tar=0;
								break;
							}
							
						}
						if (tar==1){       //������  ����Ա�״̬���У�������Ԫ��
							State temp=new State(end);
							state_table.add(end);
							sts.state_set.add(temp);
							out2.write(element+"  ");						
							out4.write(symbol+" ");
							element="";
							symbol="";
							end="";
						}
						continue;
					}
				}
		for (int i=0;i<sts.state_set.size();i++){//����Ԥ���дʵ�����
			out1.write(state_table.get(i)+"  ");
			TOTAL_NUM=TOTAL_NUM+sts.state_set.get(i).num; 
		}
		
		STATE_NUM=sts.state_set.size();  //״̬����������������
	//	System.out.println(sts.state_set.size());
	//	System.out.println(TOTAL_NUM);
		in.close();
		out1.close();
		out2.close();
		out4.close();
		
	}
	
	//--------------------------------------------------------------------------------------------------
	// ����״̬ת�ƾ��� �����ַ���ϣ��  ��������ɷ�ģ�͵Ĳ���  
	public void structAB(double delta)throws IOException{           //add delta ƽ��
		A_STATE=new int [STATE_NUM][STATE_NUM];  //״̬ת���� �����ִ�����
		A_table=new double [STATE_NUM][STATE_NUM]; // ״̬ת���� (Ƶ��)
		FileWriter out = new FileWriter(outfile3);  
		FileWriter out1 = new FileWriter (outfile6);
		for (int i=0;i<STATE_NUM;i++){             //��ʼ��
			for (int j=0;j<STATE_NUM;j++){
				A_STATE[i][j]=0;
			}
		}
			
			
		String stateF="";  //ǰ�ʴ�
		String stateB="";  //��ʴ�
		String endF="";    //ǰ�ʴ���
		String endB="";    //��ʴ���
		int indexF=0;      //ǰ�ʴ����ڴ��Ա��е�����
		int indexB=0;      //��ʴ����ڴ��Ա��е�����
		FileReader in = new FileReader(outfile2);
		int ch=0;	
		while ((char)(ch=in.read())!=' '){ //ȡ�õ�һ��Ҫ�����ϣ��Ĵʴ�
			stateF=stateF+(char) ch; 
		}
		endF=stateF.substring(stateF.indexOf("/"),stateF.length()); //���ʴ������ϣ��
		Symbol temp=new Symbol(stateF);
		temp.end=endF;
		temp.p=(double)temp.num/(double)TOTAL_NUM;
		B_map.put(stateF, temp);		
		while ((ch=in.read())!=-1){
			if ((char)(ch)==' '){
				continue;
			}
			
			stateB=stateB+(char) ch;
			while ((char) (ch = in.read())!=' '){
				stateB=stateB+(char) ch;              //ȡ��һ���´ʴ�
			}
				endB=stateB.substring(stateB.indexOf("/"),stateB.length());
				indexF=state_table.indexOf(endF);        
				indexB=state_table.indexOf(endB);
				A_STATE[indexF][indexB]++;           //����״̬ת�ƾ���(ת�ƴ���)
				if (B_map.containsKey(stateB)){     
					temp=(Symbol) B_map.get(stateB);  //���¹�ϣ��   ����ʴ��Ѿ������ڹ�ϣ��
					temp.num++;           //�ʴ����ִ�������
					temp.p=(double)temp.num/(double)TOTAL_NUM;//�ʴ�����Ƶ�ʸ��¡�
					B_map.put(stateB, temp);
				}
				else{                         //����ʴ��������ڹ�ϣ����
					temp=new Symbol(stateB);
					temp.end=endB;
					temp.p=(double)temp.num/(double)TOTAL_NUM;
					B_map.put(stateB, temp);
				}
				stateF=stateB;
				endF=endB;
				stateB="";
				endB="";
	}
		Collection setv=B_map.values();  //������ϣ����ֵ��ӡ���ļ���
		Iterator iterator=setv.iterator();
		while(iterator.hasNext()){
			Symbol temp3=(Symbol)iterator.next();
			out.write(temp3.symbol+" "+temp3.num+" "+temp3.end+"    "+temp3.p+'\r'+'\n');
		}
		out.close();
		in.close();
		Un_Reg = new double [STATE_NUM];   //����δ��½�� ƽ����
		for (int i=0;i<STATE_NUM;i++){
			Un_Reg[i]=delta/(double) TOTAL_NUM;
			out1.write(Un_Reg[i]+"       ----");
			for (int j=0;j<STATE_NUM;j++){
				A_table[i][j]=(double) A_STATE[i][j]/(double) TOTAL_NUM;
				out1.write("  "+A_table[i][j]);
			}
			out1.write(""+'\r'+'\n');
		}
		
		out1.close();
	//	System.out.println("2");
	}
//------------------------------------------------------------------------------------------------------------
//            viterbi�㷨 ʵ�ֱ�ע	
	public void Viterbi()throws IOException{
		FileReader in = new FileReader(infile2);
		FileWriter out = new FileWriter(outfile5);
		int ch;
		int n=0;//�����д��صĸ���
		String sentence="";         // һ������ע����
		while ((ch=in.read())!=-1){
			if ((char)ch==' '||(char)ch=='\r'||(char)ch=='\n'){  
				continue;
			}
			n=0;
			sentence=sentence+(char) ch; 
			while ((char)(ch=in.read())!='��'&&(char)(ch)!='��'&&(char)(ch)!='��'&&(char)(ch)!='��'&&(char)(ch)!='��'&&(char)(ch)!='\n'&&(char)(ch)!='\r'&&ch!=-1){
				sentence=sentence+(char) ch;
				if ((char)ch==' '){
					n++;
				}
			}
			//
		    if (ch!=-1){
		    	sentence=sentence+(char) ch;
		    }
			if (sentence.endsWith("��")||sentence.endsWith("��")||sentence.endsWith("��")||sentence.endsWith("��")||sentence.endsWith("��")){
				n++;
				sentence=sentence+" ";
			}
			String [] words=sentence.split(" ");	//�������д�ž��ӵĴ�
			//------------ viterbi�㷨��ʼ
			String word_state="";   //����ʣ����� ��
			Symbol temp=new Symbol();
			Vit_var vit_array[][]= new Vit_var[n][STATE_NUM]; //ά�رȱ�������
			Vit_path path_array[][]=new Vit_path[n][STATE_NUM]; //·������ path_array[i][j]��ʾ��i+1������j���Ե�ʱ���i���ʵĴ��ԡ�
			for (int i=0;i<STATE_NUM;i++)  //��ʼ��ά�ر��㷨������ʼ��
			{
				vit_array[0][i]=new Vit_var();
				vit_array[0][i].index=0;
				vit_array[0][i].p=((double)sts.state_set.get(i).num)/((double)TOTAL_NUM);
				
				vit_array[0][i].state=sts.state_set.get(i).state;
				word_state=words[0]+vit_array[0][i].state;  //���ɴʴ�
				
				if((temp=(Symbol)B_map.get(word_state))!=null) //�ʴ��ڹ�ϣ����
				{
					vit_array[0][i].p=vit_array[0][i].p*temp.p;
				}
				else         //�ʴ���δ��½��
				{
					vit_array[0][i].p=vit_array[0][i].p*Un_Reg[i];
				}
			}
			//--------------------------------------------
			double p_max=0.0;  //����
			String s_now="";   //����
			String s_max="";
			double p_now=0.0;
			for (int i=1;i<n;i++) //���ÿ���ʵ�ѭ��
			{
				for (int j=0;j<STATE_NUM;j++) //���ÿ�ִ��Ե�ѭ��
				{
					vit_array[i][j]=new Vit_var();
					word_state=words[i]+sts.state_set.get(j).state;
					p_max=0.0;
					for (int k=0;k<STATE_NUM;k++)    //Ѱ�Ҹ�������ѡ��
					{
						p_now=vit_array[i-1][k].p*A_table[k][j];
						s_now=vit_array[i-1][k].state;
						if ((temp=(Symbol)B_map.get(word_state))!=null) //��ϣ���еĴʴ�
						{
							p_now=p_now*temp.p;
						} 
						else   //δ��½�ʴ�
						{
							p_now=p_now*Un_Reg[j];
						}
						if (p_max<p_now)
						{
							p_max=p_now;
							s_max=s_now;
						}
						/////////////////////////////////////
						
					}
					vit_array[i][j].p=p_max;  //����һ��vit_array��path_array��
					vit_array[i][j].index=i;
					vit_array[i][j].state=sts.state_set.get(j).state;
					path_array[i-1][j]=new Vit_path();
					path_array[i-1][j].index=i-1;
					path_array[i-1][j].state_Front=s_max;
				}
			}
			//***** ������������
			String []state_result=new String[n]; //�洢���Ա�ע�Ľ��
			for(int i=0;i<n;i++)
			{
				state_result[i]="";
			}
			p_max=0.0;
			int index=0;
			for (int i=0;i<STATE_NUM;i++)         //Ѱ�Ҹ������Ľ��
			{
				if (vit_array[n-1][i].p>p_max)
				{
					p_max=vit_array[n-1][i].p;
					index=i;
				}
			}
			state_result[n-1]=sts.state_set.get(index).state;
			for (int i=n-2;i>=0;i--)  //��ʼѭ������������Ա�ע�������
			{
				state_result[i]=path_array[i][index].state_Front;
				for (int j=0;j<sts.state_set.size();j++)
				{
					if (sts.state_set.get(j).state==path_array[i][index].state_Front)
					{
						index=j;
						break;
					}
				}
			}
						//-------------------- viterbi�㷨����
			for (int i=0;i<n;i++){ //���ļ���������
				out.write(words[i]+state_result[i]+" ");
			}
			out.write(""+'\r'+'\n');
			sentence="";
			
		}
		out.close();
		in.close();
	}
	
	//----------------------------------------------------------------------------------
	//  �����㷨Ч��
	public void score()throws IOException{
		FileReader in_source=new FileReader(outfile2); //Դ�ļ�
		FileReader in_result=new FileReader(outfile5); //����ļ�
		String source="";//Դ�е�һ���ʴ�
		String result="";//�ұ�ע�����һ���ʴ�
		
		int ch_s;
		int ch_r=0;
		
		while (ch_r!=-1){
			while ((char)(ch_r=in_result.read())==' '||(char)ch_r=='\r'||(char)ch_r=='\n'){
				continue;
			}
			if(ch_r==-1){
				 correct_rate=(double)((double)correct_words)/((double)total_words);
					in_source.close();
					in_result.close();
					System.out.print("׼ȷ��Ϊ��");
					System.out.println(correct_rate);
					System.out.println("��ȷ��ע������"+correct_words+"  "+"���Ʊ�ע������"+total_words);
				return;
			}
			result=result+(char) ch_r;
			while ((char)(ch_s=in_source.read())==' '||(char)ch_s=='\r'||(char)ch_s=='\n'){
				continue;
			}
			source=source+(char) ch_s;
			
			while((char)(ch_r = in_result.read()) != ' '){
				result=result+(char) ch_r;
			}
			while ((char)(ch_s=in_source.read())!=' '){
				source=source+(char)ch_s;
			}
			
			
			if (source.equals(result)){
				correct_words++;
			}
			total_words++;
			result="";
			source="";			
		}		
	}
	
	
	//----------------------------------------------------------------------------------
	//������
	public static void main (String[] args)throws IOException {
		struct_S_V test = new struct_S_V();
		test.structSV();
		test.structAB(0.0001);
		test.Viterbi();
		test.score();
		System.out.println("���");
	}
}
