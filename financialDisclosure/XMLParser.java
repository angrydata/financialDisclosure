package financialDisclosure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
*���ѹα� ���ڰ������� �����Ǵ� ������ ������ ������ txt ������ �������� ��ȯ�� �� �ֽ��ϴ�. ��ȸ�� �����, ���������� ������ ���� ���ĸ� ����ϴٸ� ��ȯ �����մϴ�.

*���� pdf�� �� ���� ������ xml�������� export�ؾ� �մϴ�. 
*�Ʒ��ѱ��̳� �ٸ� �������� ������ ������ �ִٸ� pdf�� 1�� ��ȯ �� pdf�� xml�� export�ؼ� ����ϸ� �˴ϴ�.   
*�� �ڵ带 �̿��ؼ� ���������� ��� xml������ �����ͷ� ��ȯ�� �� �ֽ��ϴ�.

*���� �׵θ� ������ Ȯ���ϸ鼭 ������ ���� �Ǵܰ� �����ϵ��� �������� ��ȯ�ϴ� ����Դϴ�. 
*'����'��� �� ������ �ܾ ����Ͽ�����, �ᱹ if���� ���谡 �Ǿ� �ִ� �� ���عٶ��ϴ�.

*�߰��� ������ �߻��ϸ� �ڵ带 ������ �� ���� ������ ���� �پ��� �������� �ֱ� ������ �ڵ� �������� �����ϱ�� ���� ��ϴ�. 
*���� xml�� excel�� ��� �ش� �κ��� �׵θ�ó���� �߸� �Ǿ� ������(����� ������ ���� ���� ǥ�� �̻��ϰ� �Ǿ� ������) �ش� �κ��� �׵θ��� �����͸� ������ ������ �� xml�� �״�� �����Ͽ� �ٽ� �� �ڵ带 �����غ��� ������ ��� �� �˴ϴ�.

*�ָ��� ��찡 �߻��Ҷ��� ��� �޽����� ��µǵ��� �Ͽ� ������ ���� �ǵ��� �߽��ϴ�.

*������ ���������������Ϻ�ȯ�� ����ȭ�� �ڵ��Դϴ�. ������ ���캸�� �ƽð�����, �׸� ����Ʈ�� �ڵ尡 �ƴϴ�, �� ���� �ڵ�� �������ֽ� ���� �ִٸ� ���� ȯ���մϴ�. 

*�ٸ� ���� ������ ���, �ٸ� ����ڵ��� ���ؼ� �� ������� ��������ּ���.
 * @author vuski@github
 **/

public class XMLParser {

	public static void main(String[] args) throws IOException {
		
		String fileLocationSource = "D:\\���������� ���\\data\\2017\\"; //xml ���� ���
		String fileName = "��⵵.xml"; //xml ���ϸ�
		
		//������ �о�� �� �⺻���� ��ó���۾��� �Ѵ�.
		String text = readFileText(fileLocationSource+fileName, "UTF-8").replaceAll("&#10;","");
		text = text.replaceAll(">[\\s]+<","><").replaceAll("\r", "").replaceAll("\n","").replaceAll("     "," ");
		//System.out.println(text);
				
		FileOutputStream fos = new FileOutputStream(fileLocationSource+fileName+"_converted.txt"); //������ ���� �̸�
		OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		
			
		int index = text.indexOf("<Row");
		String sid ="";
		
		//xml���� �� �κ��� ��Ÿ���� �о�´�.
		HashSet<String> bottomBorders = getStylesBottom(text); 
		HashSet<String> rightBorders = getStylesRight(text);
		//for (String o:bottomBorders) System.out.println(o);	
		
		L2 : while(true) {  //�� while loop��  ����� �ѹ��� ���� ��
			
			ArrayList<String> cellArray;			
			String row = getRow(text,index); //�ϴ� �� ���� �޾ƿ´�. ����!
			//System.out.println(row);
			
			//�⺻�� ����. �� ���� ���� �ƴ���, �ƹ� ������ ������ ������
			if (row.equals("exit!")) { //�� �̻� ������ exit ����
				break L2;
			} else if (row.indexOf("continue")!=-1) { //�ƹ�������� row�� ������
				index = Integer.parseInt(row.split("\\|")[1]);				
				continue L2;
			}
			
			index = index+row.length();
			//System.out.println(text.substring(index, index+4));
			String rowContents = getContents(row).replaceAll(" ","");
			//System.exit(0);;
			int checkA1 = rowContents.indexOf("�Ҽ�");
			int checkA2 = rowContents.indexOf("����");
			int checkA3 = rowContents.indexOf("����");
			
			String[] result1 = new String[4];
			System.out.println(rowContents);
			if (checkA1!=-1 && checkA2!=-1 && checkA3!=-1) { //�ҼӰ� ������ ������ �������� �Ѿ�� '�Ҽ�'�� ������ ù ó��
				result1[0] = rowContents.substring(checkA1+2, checkA2); //�Ҽ�
				result1[1] = rowContents.substring(checkA2+2, checkA3);  //����
				result1[2] = rowContents.substring(checkA3+2);			//����
			} else {
				
				continue L2;
			}
			
			String cell="", contents;			
			
			//System.out.println("���� "+index);
			row = getRow(text,index); //'õ��' �κ��̹Ƿ� �ѱ��
			//System.out.println(text.substring(index, index+4));
			index = index+row.length();
			
			row = getRow(text,index); //���ΰ��� ���� ����̹Ƿ� �ѱ��
			//System.out.println(index+row);
			//System.out.println(text.substring(index, index+4));
			String rowContentTemp = getContents(row).replaceAll(" ","");
			boolean existBefore = rowContentTemp.indexOf("��������")!=-1? true:false;  ////��Ȥ �ű� �������� ��� ���������� ���� ��찡 �ִ�.
			index = index+row.length();
			
			//System.out.println(index+row);
			row = getRow(text,index); //������,���Ҿ��̹Ƿ� �ѱ��
			//System.out.println((row));
			if (row.indexOf("��")==-1) {  //�����ݾ׾�� �ε��� ���� ���� �ű������ڶ����� �ִ°���
				index = index+row.length(); 
			}
			//System.out.println(index+row);			
			
			
			L1 : while (true) {  //�� ������ ����� ���� �ϳ��� �ٷ�� ����
				
				row = getRow(text,index);
				if (row.indexOf("continue")!=-1) { //�ƹ�������� row�� ������
					index = Integer.parseInt(row.split("\\|")[1]);
					//System.out.println(result1[2]+"_"+row);
					continue L1;
				}
				//System.out.println(index+row);				
				//System.out.println(row);
				
				rowContents = getContents(row); 
				checkA1 = rowContents.indexOf("�Ҽ�");
				checkA2 = rowContents.indexOf("����");
				checkA3 = rowContents.indexOf("����");
				
				//System.out.println(rowContents);
				if (checkA1!=-1 && checkA2!=-1 && checkA3!=-1) {
					System.out.println("�Ҽӵ��� "+index);
					//System.out.println(text.substring(index, index+4));
					break L1; //�Ҽ��̸� ������ ����.���� ������� �Ѿ�ٴ� �ǹ���	
					
				} else if (row.replaceAll(" ","").indexOf(">�Ѱ�<")!=-1 
						||row.replaceAll(" ","").indexOf(">���Ѱ�<")!=-1
						||getContents(row).indexOf("���Ѱ�(�Ұ�)")!=-1) { //�̰��� �±� ���� ����. ���� ���� �Ѱ�� �����ϱ� ����
					index = index+row.length();
					//System.out.println("�Ѱ���� "+index);
					break L1; //�Ѱ谡 �����ص� �������� �Ѿ.
				} else {					
					rowContents = row.replaceAll(" ","").replaceAll("\\(�Ұ�\\)","");
					if (rowContents.indexOf("��")!=-1 && rowContents.indexOf("�Ѱ�")==-1){  //����������� ���Ѱ�. �� ����Ѵ�. ex. 2016_05.xml �����
						//contents = contents.replaceAll(" ","");
						result1[3]= getContents(getCellArray(row).get(0)).replaceAll("��","").replaceAll(" ","").replaceAll("\\(�Ұ�\\)","");
						//result1[3] = rowContents.substring(rowContents.indexOf("��")+1, rowContents.indexOf("<",rowContents.indexOf("��")+1));
						//System.out.println("����<"+result1[3]+">");
						index = index+row.length();
						continue L1;
					} 
				}
				
				//���⼭���� ��� �󼼸� �д´�.
				ArrayList<String> category = new ArrayList<String>();
				ArrayList<String> who = new ArrayList<String>();
				ArrayList<String> what = new ArrayList<String>();
				ArrayList<String> detail = new ArrayList<String>();
				ArrayList<String> priceBefore = new ArrayList<String>();
				ArrayList<String> priceRise = new ArrayList<String>();
				ArrayList<String> priceFall = new ArrayList<String>();
				ArrayList<String> priceCurrent = new ArrayList<String>();
				ArrayList<String> reason = new ArrayList<String>();
				String reason_ = "";
				
				String who_ = "";
				String what_ = "";
				String detail_ = "";
				String priceBefore_ = "";
				String priceRise_ = "";
				String priceFall_ = "";
				String priceCurrent_ ="";
				
				int countIndex = 0;
				int sixColumnCount = 0;
				
				//xml������ ���� ������ merge�Ǿ� ó���� �κе��� �ټ� �ִ�. ����������� ������ ���ۺ��� ������ ���� �����Ͱ� ������ ���� ������� �ʰ� �¿�� �Դٰ��� �ϱ� �����̴�.
				//�׷����� �ұ��ϰ� ������ ���� ������ �������� ������ �� �ִ�. �׷��� ������ �����ϱ� ����, ������� �Ʒ��� �� ������ �̿��Ͽ� merge���� �� �������鼭 ������ ���������� ���ڸ��� ã�ư����� �Ѵ�.
				int[] mergedownCount = {0,0,0,0,0,0,0,0};
				int[] check = {0,0,0,0,0,0,0,0};
				
				L3 : while (true) {   // ����Ǵ� �Ҵ����� �ٷ��.
					//������� �� �����۾� ó��
					
					row = getRow(text,index);
					//System.out.println(row);
					
					if (row.indexOf("��")!=-1&&getContents(row).indexOf("�Ѱ�")==-1) {
						if (reason.size()<who.size()) { //���� �Ұ���� �з��������� ���յǾ��� ��� 2016_05.xml �� ������
							for (int j=0 ; j <sixColumnCount ; j++) {
								reason.add(reason_);
								//System.out.println("reason:"+reason_);
							}
							reason_="";
							sixColumnCount =0;
						}
						break L3;
					} else if (row.replaceAll(" ","").indexOf(">�Ѱ�<")!=-1 
							||row.replaceAll(" ","").indexOf(">���Ѱ�<")!=-1
							||getContents(row).indexOf("���Ѱ�(�Ұ�)")!=-1) { //�̰��� �±� ���� ����. ���� ���� �Ѱ�� �����ϱ� ���� //2013_5.xml ������
						//System.out.println("�Ѱ���� "+index);
						break L3; //�Ѱ谡 �����ص� �������� �Ѿ.
					} else if (getContents(row).indexOf("continue")!=-1) { // ���� �� ���̸�,
						System.out.println("�̰� ��µǸ� ������ ������ ������ �����Ͽ� Ȯ���غ���++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
						index = Integer.parseInt(row.split("\\|")[1]);
						//System.out.println(text.substring(index, index+15));
						continue L3; //�� ���� ���� �� ��������� ������, continue�� break�� ������ ������ �Ѵ�. 2013_4.xml Ȳ��ö. ���⼭�� continue
					}
					
					
					int i=0;
					int mergedowned;
					cellArray = getCellArray(row);
					if (getContents(cellArray.get(0)).equals("") 
							&& getMergedown(cellArray.get(0))!=0
							&& cellArray.size()==9) {  //ù���� ��ü������ �з� ������,
						cellArray.remove(0);
					}
					
					
					//���� ��� �����
					if (mergedownCount[0]==0) {
						cell = cellArray.get(i++);
						
						sid = getSID(cell);
						mergedowned = getMergedown(cell);
						mergedownCount[0] += mergedowned;
						contents = getContents(cell);
						who_ += contents;
						if (bottomBorders.contains(sid)) check[0] = 1; //������ ������ ������ ���
					
					} else {
						mergedownCount[0]--;
					}
					
					
					//����� ����
					if (mergedownCount[1]==0) {
						cell=cellArray.get(i++);
						sid = getSID(cell);
						mergedowned = getMergedown(cell);
						mergedownCount[1] += mergedowned;
						contents = getContents(cell);
						//System.out.println(contents);
						what_ += contents;
						if (bottomBorders.contains(sid)) check[1] = 1; //������ ������ ������ ���
						
					} else {
						mergedownCount[1]--;
					}
					
					//��� ������
					if (mergedownCount[2]==0) {
						cell=cellArray.get(i++);
						//System.out.println(cell);
						sid = getSID(cell);
						mergedowned = getMergedown(cell);
						mergedownCount[2] += mergedowned;
						contents = getContents(cell);
						detail_ += contents;
						
						while (!rightBorders.contains(getSID(cell))) {
							System.out.println("������ �׵θ� ����++++++++++++++++�Ʒ� ��ºκ� �����Ͽ� Ȯ���ؾ� �Ѵ�.+++++++++++++");
							cell=cellArray.get(i++);
							contents = getContents(cell);
							System.out.println("contents:"+contents);
							detail_ += contents;
						}
						//System.out.println("detail:"+detail_);
						if (bottomBorders.contains(sid)) check[2] = 1; //������ ������ ������ ���
						
					} else {
						mergedownCount[2]--;
					}
					//System.out.println(cell);
					//����				
					if (existBefore) {  //�������� ������ ������ ������
						
						//��������
						if (mergedownCount[3]==0) {
							cell=cellArray.get(i++);
							sid = getSID(cell);
							mergedowned = getMergedown(cell);
							mergedownCount[3] += mergedowned;
							contents = getContents(cell);
							priceBefore_ += contents;
							//System.out.println("priceBefore:"+priceBefore_);
							if (bottomBorders.contains(sid)) check[3] = 1; //������ ������ ������ ���
							
						} else {
							mergedownCount[3]--;
						}
						
						
						//������(�ǰŷ���)
						if (mergedownCount[4]==0) {
							cell=cellArray.get(i++);
							sid = getSID(cell);
							mergedowned = getMergedown(cell);
							mergedownCount[4] += mergedowned;
							contents = getContents(cell);
							priceRise_ += contents;
							//System.out.println("priceRise:"+priceRise_);
							if (bottomBorders.contains(sid)) check[4] = 1; //������ ������ ������ ���
							
						} else {
							mergedownCount[4]--;
						}
						
						//���Ҿ�(�ǰŷ���)
						if (mergedownCount[5]==0) {
							//System.out.println(cell);
							cell=cellArray.get(i++);
							sid = getSID(cell);
							mergedowned = getMergedown(cell);
							mergedownCount[5] += mergedowned;
							contents = getContents(cell);
							priceFall_ += contents;
							//System.out.println("priceFall:"+priceFall_);
							if (bottomBorders.contains(sid)) check[5] = 1; //������ ������ ������ ���
							
						} else {
							mergedownCount[5]--;
						}
						
					} else { //��Ȥ �ű� �������� ��� ���������� ���� ��찡 �ִ�.
						//System.out.println("�ȵǴµ�");
						priceBefore_ = "";
						priceRise_ = "";
						priceFall_ = "";
					}
					
					//���簡��
					if (mergedownCount[6]==0) {
						//System.out.println(cell);
						cell=cellArray.get(i++);
						sid = getSID(cell);
						mergedowned = getMergedown(cell);
						mergedownCount[6] += mergedowned;
						contents = getContents(cell);
						priceCurrent_ += contents;
						//System.out.println("priceCurrent:"+priceCurrent_);
						if (bottomBorders.contains(sid)) check[6] = 1; //������ ������ ������ ���
						
					} else {
						mergedownCount[6]--;
					}
					
					
					//��������
					if (mergedownCount[7]==0) {
						cell="";
						try { //���� �̻��ϰ� ���� ��츦 ����� 2016_02.xml ���������� �߻�
							cell=cellArray.get(i++);
							sid = getSID(cell);
							mergedowned = getMergedown(cell);
							mergedownCount[7] += mergedowned;
							contents = getContents(cell);
							reason_ += contents;
							//System.out.println("reason:"+reason_);
						} catch (Exception e) {
							//System.out.println("���� ���� ���� �߻�");
						}
						
						//System.out.println("reason �߰�:"+reason_);
						if (bottomBorders.contains(sid)) check[7] = 1; //������ ������ ������ ���
						
					} else {
						mergedownCount[7]--;
					}
					
					
					
					//���� ���� �������� üũ 
					if (mergedownCount[0]==0 && check[0]==1) { //ù���� �������� �������� ��� ������ ���̶�� �����Ѵ�.
						
						if (priceCurrent_.equals("")&&!detail_.equals("�����ź�")&&!detail_.equals("�������")) { //�̰� ��� �ִٸ� ��ٲ� ������ ����. ���� ���� ���� ������
							
							//System.out.println(countIndex+"/"+detail_);
							detail.set(countIndex-1, detail.get(countIndex-1)+detail_);
							priceBefore.set(countIndex-1, priceBefore.get(countIndex-1)+priceBefore_);
							priceRise.set(countIndex-1, priceRise.get(countIndex-1)+priceRise_);
							priceFall.set(countIndex-1, priceFall.get(countIndex-1)+priceFall_);
							priceCurrent.set(countIndex-1, priceCurrent.get(countIndex-1)+priceCurrent_);
							
							if (reason.size()<countIndex) { //���� �ϳ��� ���� �� �ɸ��� ������. 2013_a.xml �ڼ�ȿ
									reason_ ="";		
							} else {
								if (reason_.equals("")) reason_ = reason.get(countIndex-1); //�� ���� ���� �����´�.				
							}
							//System.out.println("���⿡ ���͹���");
							
						} else {
							
							category.add(result1[3]);
							
							who.add(who_);
							what.add(what_);
							detail.add(detail_);
							priceBefore.add(priceBefore_);
							priceRise.add(priceRise_);
							priceFall.add(priceFall_);
							priceCurrent.add(priceCurrent_);
							
							sixColumnCount++;
							countIndex++; // '��������'�� ������ ���� ī��Ʈ�Ѵ�.
						}
						
						who_="";
						what_="";
						detail_="";
						priceBefore_="";
						priceRise_="";
						priceFall_="";
						priceCurrent_="";
					}
					
					if (mergedownCount[7]==0 && check[7]==1) {
						
						//System.out.println("����Ѵ�");
						for (int j=0 ; j <sixColumnCount ; j++) {
							reason.add(reason_);
							//System.out.println("reason:"+reason_);
						}
						reason_="";
						sixColumnCount =0;
						
					}
					
					index = index+row.length();
					//System.out.println(text.substring(index, index+4));
					
				} //L3 ��� �ϳ���
				
				String reasonTemp ="�̰� ������ �ȵȴ�.";
				
				for (int j=0 ; j< who.size() ; j++) {
					
					//System.out.println(j);
					bw.write(result1[0]+"|"+result1[1]+"|"+result1[2]+"|"+category.get(j)+"|");
					String priceBeforeTemp = checkNumber(priceBefore.get(j));
					String[] priceRiseTemp =checkNumber2(priceRise.get(j)).split("\\|");
					String[] priceFallTemp =checkNumber2(priceFall.get(j)).split("\\|");
					String priceCurrentTemp = checkNumber(priceCurrent.get(j));
					
/*
					System.out.print(result1[0]+"|"+result1[1]+"|"+result1[2]+"|"+category.get(j)+"|");
					System.out.print(who.get(j)+"|"+what.get(j)+"|"+detail.get(j)
					+"|"+priceBeforeTemp+"|"+priceRiseTemp[0]+"|"+priceRiseTemp[1]+"|"+priceFallTemp[0]+"|"+priceFallTemp[1]
					+priceCurrent.get(j)+"|");
*/				
					
					
					if (category.get(j).equals("ä��")) {
						bw.write(who.get(j)+"|"+what.get(j)+"|"+detail.get(j)
						+"|"+(priceBeforeTemp.replaceAll(" ","").equals("")?"":("-"+priceBeforeTemp))
						+"|"+(priceRiseTemp[0].replaceAll(" ","").equals("")?"":("-"+priceRiseTemp[0]))
						+"|"+(priceRiseTemp[1].replaceAll(" ","").equals("")?"":("-"+priceRiseTemp[1]))
						+"|"+(priceFallTemp[0].replaceAll(" ","").equals("")?"":("-"+priceFallTemp[0]))
						+"|"+(priceFallTemp[1].replaceAll(" ","").equals("")?"":("-"+priceFallTemp[1]))
						+"|"+(priceCurrentTemp.replaceAll(" ","").equals("")?"":("-"+priceCurrentTemp))
						+"|");
					} else {
						bw.write(who.get(j)+"|"+what.get(j)+"|"+detail.get(j)
						+"|"+priceBeforeTemp+"|"+priceRiseTemp[0]+"|"+priceRiseTemp[1]+"|"+priceFallTemp[0]+"|"+priceFallTemp[1]+"|"
						+priceCurrentTemp+"|");
					}
					
					
					
					if (reason.get(j).equals("��")) {
						
					} else {
						reasonTemp = reason.get(j); 
					}
					//System.out.println(reasonTemp);
					bw.write(reasonTemp);
					
					
					bw.newLine();

				}
				
			} //while L1 ������
			
			
			
		} //while L2 �������
		
		bw.close();
		
		
	

	}
	
	private static String getSID(String cell) {
		
		int iS = cell.indexOf("ss:StyleID=")+12;
		int iE = cell.indexOf("\"", iS);
		
		return cell.substring(iS,iE);
	}

	private static HashSet<String> getStylesBottom(String text) {
		
		HashSet<String> bottomBorders = new HashSet<String>();
		
		int index = text.indexOf("<Style ss:");
		String criteria = "Border ss:Position=\"Bottom\"";// ss:Color=\"#231F20\"";  //2011���� 231f20
		
		while (true) {
			String style = getStyleRow(text, index);
			if (style.equals("exit!")) break;
			//System.out.println(style);
			index = index + style.length();
			
			int iS = style.indexOf("ss:ID")+7;
			int iE = style.indexOf("\"", iS);
					
			String id = style.substring(iS, iE);
			if (style.indexOf(criteria)!=-1) bottomBorders.add(id);			
		}	
		
		return bottomBorders;
	}
	
	private static HashSet<String> getStylesRight(String text) {
		
		HashSet<String> rightBorders = new HashSet<String>();
		
		int index = text.indexOf("<Style ss:");
		String criteria = "Border ss:Position=\"Right\"";//ss:Color=\"#231F20\"";
		
		while (true) {
			String style = getStyleRow(text, index);
			if (style.equals("exit!")) break;
			//System.out.println(style);
			index = index + style.length();
			
			int iS = style.indexOf("ss:ID")+7;
			int iE = style.indexOf("\"", iS);
					
			String id = style.substring(iS, iE);
			if (style.indexOf(criteria)!=-1) rightBorders.add(id);			
		}	
		
		return rightBorders;
	}

	private static String getStyleRow(String text, int index) {
		
		int indexS = text.indexOf("<Style", index);
		if (indexS==-1) return "exit!";
		
		int indexE = text.indexOf("</Style>", indexS);
		
		String row = text.substring(indexS, indexE+8); //</row>���� ����. ���� ������ ����.	
		
		return row;
		
	}

	private static int getMergedown(String cell) {
		
		int check1 = cell.indexOf("MergeDown");
		if (check1==-1) return 0;
		String temp = cell.substring(check1+11,cell.indexOf("\"",check1+11));		
		return Integer.parseInt(temp);
	}
	
	private static int getMergeAcross(String cell) {
		
		int check1 = cell.indexOf("MergeAcross");
		if (check1==-1) return 0;
		String temp = cell.substring(check1+13,cell.indexOf("\"",check1+13));		
		return Integer.parseInt(temp);
	}

	private static String checkNumber2(String contents) {
		
		contents = contents.replaceAll(",", "");
		contents = contents.replaceAll(" ", "");
		contents = contents.replaceAll("-", "0");
		
		int i = contents.indexOf("(");
		
		if (i==-1) {
			return contents+"| ";
		} else {
			contents = contents.substring(0, contents.length()-1);
			return contents.split("\\(")[0]+"|"+contents.split("\\(")[1];
		}
		
	}

	private static String checkNumber(String contents) {
		
		contents = contents.replaceAll(",", "");
		
		if (contents.equals("-")) return "0";
		
		return contents;
		
	}

	private static String getContents(String cell) {
		
		cell = cell.replaceAll("<[^>]*>", ""); //�±�����
		return cell;
				
		
	}
	
	private static String getContents_(String cell) {
		
		int check4,check5;
		cell = cell.replaceAll("><", "");
		check4 = cell.indexOf(">");
		check5 = cell.indexOf("<",1);
		
		if (check4 == cell.length()-1) {
			return "";
		} else {
			return cell.substring(check4+1,check5).replaceAll("  "," ");					
		}			
		
	}

	private static String getRow(String text, int index) {
		
		int check1,check2;
		
		int indexS = text.indexOf("<Row",index);		
		if (indexS==-1) return "exit!";
		//System.out.println(text.substring(indexS,indexS+4));
		
		check1 = text.indexOf("<",indexS+1);
		check2 = text.indexOf("/>",indexS+1);
		if (check2!=-1 && check2<check1) {
			index = check2+2;
			return "continue|"+index; //�ƹ� ������� row�� ������
		}
		
		int indexE = text.indexOf("</Row>", indexS+1);
		
		String row = text.substring(indexS, indexE+6); //</row>���� ����. ���� ������ ����.	
		if (row.replaceAll("<[^>]*>", "").equals("")) return "continue|"+(indexS+row.length());
		//System.out.println("____"+ row);
		return row;
	}

	private static ArrayList<String> getCellArray(String row) {
		
		int check3,check4,check5,check6,check7;
		int indexSub = 0;
		
		check3 = row.indexOf(">")+1;
		String cells = row.substring(check3); // cell �±׵鸸 �����
		ArrayList<String> cellArray = new ArrayList<String>();
		
		L1: while(true) {
			//System.out.println(indexSub);
			check4 = cells.indexOf("<Cell",indexSub);
			if (check4==-1) break L1;
			
			check5 = cells.indexOf("/>",indexSub);
			check6 = cells.indexOf("<", indexSub+1);
			check7 = cells.indexOf("</Cell>", indexSub);
			if (check5!=-1 && check5<check6) {
				cellArray.add(cells.substring(check4, check5+2));
				//System.out.println(cells.substring(check4, check5+2));
				indexSub = check5+2;
				continue L1;
			} else {
				cellArray.add(cells.substring(check4, check7+7));
				//System.out.println(cells.substring(check4, check7+7));
				indexSub = check7+7;
				continue L1;	
			}
			
		}
		return cellArray;
	}

	private static String readFileText(String fileName, String charset) throws IOException {
		
		FileInputStream fis = new FileInputStream(fileName);
		InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		BufferedReader br = new BufferedReader(isr);
		
		String line;
		StringBuffer sb = new StringBuffer();
		
		while ((line=br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		} //while
		br.close();
		
		return sb.toString();
	}
	

}
