package Source;
/**
 * @author cedar
 * @version 1.0
 * @since 12/9/2015
 * @category:�ַ���
 */
public class Symbol {
	String symbol;           //�����  �� ������
	int num=1;               //�ô���Ϊ�ô���ʱ���ִ���  ��: ����/v 3  ������Ϊ���ʳ�����3��
	String end;              //������� �� ��/v
	double p=0.0;            //�ô��ڸô����³��ֵĸ���
	
	Symbol(String symbol){
		
		this.symbol=symbol;
		this.num=1;
	}
	Symbol(){
		
	}
}
