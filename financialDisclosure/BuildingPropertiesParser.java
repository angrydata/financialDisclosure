package financialDisclosure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ���� ������ ���� �ǹ� ��� �ּ� ó�� ����
 * ���������� ǥ��ȭ�Ͽ� �����س��� ���� �������� �Ѵ�.
 * ���������� ���� �����Ϳ��� �ǹ��κ��� �ּҵ鸸 ���͸��ؼ� ����+�ٿ��ֱ� �� txt������ �غ��ؾ� �Ѵ�.
 * 
 * ������ | �� ;�� ����� ����Ѵ�. �� ������ ���� ���� �� ���� �������� ������Ҵ�.
 * �� �������� ���� 2�� ���� �۾��� �ؾ� �Ѵ�.
 * ���� �Է��� ���� �״�� �ϳ��� ������ �ʰ� ����ϹǷ�, ��� ������ �������� ���� ������ ���� �ٿ������� ���� �״�� �� ����ȴ�.
 * �� ���� ���� �ּ� �߿� '��' ���ڰ� ���� ��� �� �и����� �ʴ´�.
 * @author vuski@github
 *
 */
public class BuildingPropertiesParser {
	
	private static HashMap<String, String[]> addressMap = new HashMap<String, String[]>();   //������
	private static ArrayList<String> addressList = new ArrayList<String>();
	
	private static HashMap<String, String[]> hAddressMap = new HashMap<String, String[]>();  //������
	
	private static String[] sido = {"����Ư����",
			"�λ걤����",
			"�뱸������",
			"��õ������",
			"���ֱ�����",
			"����������",
			"��걤����",
			"��⵵",
			"������",
			"��û�ϵ�",
			"��û����",
			"����ϵ�",
			"���󳲵�",
			"���ϵ�",
			"��󳲵�",
			"����Ư����ġ��",
			"����Ư����ġ��"
	};

	private static String[][] sidoShorten3 = {
			{"�����","����Ư����"},
			{"�λ��","�λ걤����"},
			{"�뱸��","�뱸������"},
			{"��õ��","��õ������"},
			{"���ֽ�","���ֱ�����"},
			{"����","��걤����"},
			{"���ֵ�","����Ư����ġ��"},	
			{"������","����Ư����ġ��"}
	};
	private static String[][] sidoShorten2 = {
			{"����","����Ư����ġ��"},
			{"����","����Ư����"},
			{"�λ�","�λ걤����"},
			{"�뱸","�뱸������"},
			{"����","����������"},
			{"��õ","��õ������"},
			{"����","���ֱ�����"},
			{"���","��걤����"},
			{"����","����Ư����ġ��"},
			{"���","��⵵"},
			{"����","������"},
			{"���","��û�ϵ�"},
			{"�泲","��û����"},
			{"����","����ϵ�"},
			{"����","���󳲵�"},
			{"���","���ϵ�"},
			{"�泲","��󳲵�"},
	};
	
	private static String description;

	public static void main(String[] args) throws IOException {
		
		String fileLocation ="D:\\���������� ���\\�ǹ� �м�\\";
		String fileName = "�ǹ�_2017_sample.txt";
		
		ArrayList<String> source = readFileText(fileLocation+fileName);
		
		ArrayList<String> parsed = new ArrayList<String>();
		
		System.out.print("read ������...");
		readAddressList("pnu_code.txt");	
		//System.out.println(addressMap.get("����Ư����"));
		System.out.println("ok!");
		
		System.out.print("read ������...");
		readHAddressList("hAddress_code.txt");	
		//System.out.println(addressMap.get("����Ư����"));
		System.out.println("ok!");
		
		//���پ� �����鼭 �Ľ��Ѵ�.
		for (String line : source) {
			description ="";
			String pnu="";
			
			String parsedText = parser(line); //�� parser ���ο��� ���������� �Ϻ� �Ľ� �۾����� �����Ѵ�.
			
			String address = parsedText.split("\\|")[0];
			pnu = getPnu(address);
			
			parsed.add(parsedText+"|"+pnu+"|"+description);
		}
		
		//���
		FileOutputStream fos = new FileOutputStream(fileLocation+"parsedAddress_2017.txt");
		OutputStreamWriter osw = new OutputStreamWriter(fos,"euc-kr");
		BufferedWriter bw = new BufferedWriter(osw);
		
		// ��� ����
		// �����ּ�(������) | �����ּ�(������) | �����ּ�(����) | �����ϴ� �ּҷ� ó���� �������̳� ������ �ּ� ; ������ Ȥ�� ������ �ڵ� | ���� ������
		
		for (String line : parsed) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();
		

	}
	
	/**
	 * primary parser
	 * @param line
	 * @return
	 */
	private static String parser(String line) {
		
		
		line = sidoFixer(line); //�õ� �̸� ������ �� �� ��������
		line = basicParser(line); //��ȣ����
		line = areaParser(line);  // �ּҿ� ������ �и�
		//System.out.println(line); 
		String[] temp = line.split("\\|");
		String address = temp[0];		
		String area = "";		
		if (temp.length>1) area = temp[1];
		
		address = emdSpliter(address); //�����ּҿ� �ǹ� �̸��� �и�
		
		temp = address.split("\\|");
		if (temp[0].equals("")) {
			address = emdSpliter(temp[1].replaceAll("\\s", "")); //���������� �ٽ� ������			
		}
		
		temp = address.split("\\|");
		if (temp[0].equals("")) {			
			address = insufficientEmdSpliter(temp[1]); //�ּҺ���� �����
			//System.out.println(address);
		}
		
		if (temp.length > 1) {
			if (isOverseas(temp[1])) description += "�ܱ��ּ�;";
		}
		
		String result = address +"|"+area;
		return result;
	}
	
	private static void readHAddressList(String fileName) throws IOException {
		
		InputStream fis = BuildingParser.class.getResourceAsStream(fileName);
				//new FileInputStream(fileName);
		InputStreamReader isr = new InputStreamReader(fis,"euc-kr");
		BufferedReader br = new BufferedReader(isr);
		
		String line;
		
		while ((line=br.readLine()) != null) {
			
			String[] temp = line.split(",");
			//System.out.println(temp[0]+temp[1]+temp[2]);
			String[] value = {temp[1],temp[2]};
			hAddressMap.put(temp[0], value);
			addressList.add(temp[0]);  //������Ÿ�� �Ÿ���
			
		} //while
		br.close();
		
		
	}
	
	private static void readAddressList(String fileName) throws IOException {
		
		InputStream fis = BuildingParser.class.getResourceAsStream(fileName);
				//new FileInputStream(fileName);
		InputStreamReader isr = new InputStreamReader(fis,"euc-kr");
		BufferedReader br = new BufferedReader(isr);
		
		String line;
		
		while ((line=br.readLine()) != null) {
			
			String[] temp = line.split(",");
			//System.out.println(temp[0]+temp[1]+temp[2]);
			String[] value = {temp[1],temp[2]};
			addressMap.put(temp[0], value);
			addressList.add(temp[0]); //������Ÿ�� �Ÿ���
			
		} //while
		br.close();
		
		
	}
	private static String getPnu(String address) {
		
		address = address.replaceAll(" ","");
		String fixedAddr="";
		String pnuCode="";
		if (addressMap.containsKey(address)) {
			fixedAddr = addressMap.get(address)[0];
			pnuCode = addressMap.get(address)[1];			
		} else if (hAddressMap.containsKey(address)){
			fixedAddr = hAddressMap.get(address)[0];
			pnuCode = hAddressMap.get(address)[1];	
			description +="��������;";			
		} else { //������ �������� ������ ������Ÿ�� �Ÿ��� ����Ͽ� ������ ���� �����´�.
			
			if (!address.equals("")) {  //��������� �׳� �ǳʶڴ�.
				String lastLetter = address.substring(address.length()-1, address.length());
				
				if (lastLetter.equals("��")) {				
					description ="���θ��ּ��� Ȯ���� ŭ;";
				} else {
			
					String fixedAddress = getFixedAddr(address);	
					if (addressMap.containsKey(fixedAddress)) {
						fixedAddr = addressMap.get(fixedAddress)[0];
						pnuCode = addressMap.get(fixedAddress)[1];
						description +="���������������ּҾ���;";		
					} else if (hAddressMap.containsKey(address)){
						fixedAddr = hAddressMap.get(address)[0];
						pnuCode = hAddressMap.get(address)[1];	
						description += "���������������ּҾ���;";	
					} else {
						description += "�����ּҵ� ����";
						//System.out.println("�����ּҵ� ���� :"+beforeBunji);
					} 
					description +="������ �� ������ �ּ�ü�迡 ����;";	
				}
			}
		}
		
		return fixedAddr + ";"+ pnuCode;
	}

	private static String getFixedAddr(String addrSource) {
		
		String fixedAddress ="";
		int score = 999;
		int scoreTemp;
		
		for (String addrBank : addressList) {
			
			if (addrBank.length()<=addrSource.length()) {
				scoreTemp = getDistance(addrSource,addrBank);
			} else {
				scoreTemp = getDistance(addrBank,addrSource);
			}
			
			if (scoreTemp<score) {
				
				fixedAddress = addrBank;
				score = scoreTemp;
			}
		}	
		//System.out.println(fixedAddress);
		return fixedAddress;
	}
	


	private static String insufficientEmdSpliter(String address) {
		
		String pattern;
		Pattern p;
		Matcher m;
		
		
		pattern = 
				"([��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)";
		
		p = Pattern.compile(pattern);
		m = p.matcher(address);	
		String bldg = "";
		String addr ="";
		if (m.find()) {
			int end;
			bldg += address.substring(0,m.start(0));
			end = m.end();
			addr += address.substring(m.start(0), m.end(0));
			//System.out.println(fixedAddr);
			while (m.find()) {
				bldg += address.substring(end,m.start());
				end = m.end();
				addr += address.substring(m.start(0), m.end(0));
				//System.out.println(fixedAddr);
			}	
			bldg += address.substring(end);
			//System.out.println(fixedAddr);
		} else {
			bldg = address;
		}
		
		if (!addr.equals("")) description += "�ּҺ����ϰų� ���θ��ּ�;";
		return addr.trim() + "|" +bldg.trim();
		
	}


	private static boolean isOverseas(String address) {
		
		String pattern;
		Pattern p;
		Matcher m;		
		pattern = "[0-9a-zA-Z#,\\.\\s]{8,}"; //�������ַ� �� ���� �ּ��� 8���� �̻�
		
		p = Pattern.compile(pattern);
		m = p.matcher(address);	
		String outside = "";
		String addr ="";
		if (m.find()) {
			int end;
			outside += address.substring(0,m.start(0));
			end = m.end();
			addr += address.substring(m.start(0), m.end(0));
			//System.out.println(fixedAddr);
			while (m.find()) {
				outside += address.substring(end,m.start());
				end = m.end();
				addr += address.substring(m.start(0), m.end(0));
				//System.out.println(fixedAddr);
			}	
			outside += address.substring(end);
			//System.out.println(fixedAddr);
		} else {
			outside = address;
		}
		
		if (outside.replaceAll(" ","").equals("")) return true;
		
		return false;
	}


	private static String emdSpliter(String address) {
		
		String pattern;
		Pattern p;
		Matcher m;
		
		
		pattern = 
				"([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{1,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+(([\\s][0-9]{1,2})|([0-9]{0,2}))��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+(([\\s][0-9]{1,2})|([0-9]{0,2}))��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{1}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{1}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+(([\\s][0-9]{1,2})|([0-9]{0,2}))��[^���鸮]?)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+(([\\s][0-9]{1,2})|([0-9]{0,2}))��[^���鸮]?)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)"
				+"|([��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+��[\\s]?[��-�R]+[\\s]?[0-9]{0,2}��)";
		
		p = Pattern.compile(pattern);
		m = p.matcher(address);	
		String bldg = "";
		String addr ="";
		if (m.find()) {
			int end;
			bldg += address.substring(0,m.start(0));
			end = m.end();
			addr += address.substring(m.start(0), m.end(0));
			//System.out.println(fixedAddr);
			while (m.find()) {
				bldg += address.substring(end,m.start());
				end = m.end();
				addr += address.substring(m.start(0), m.end(0));
				//System.out.println(fixedAddr);
			}	
			bldg += address.substring(end);
			//System.out.println(fixedAddr);
		} else {
			bldg = address;
		}
		
		return addr.trim() + "|" +bldg.trim();
	}

	
	private static String areaParser(String address) {
		
		String pattern;
		Pattern p;
		Matcher m;
		
		pattern = "(��[\\s]?��[\\s]?[0-9,\\.\\s]+��([\\s]?��[\\s]?([0-9,\\.\\s]+��)?)?)"
				+ "|(��[\\s]?��[\\s]?[0-9,\\.\\s]+��([\\s]?��[\\s]?([0-9,\\.\\s]+��)?)?)"
				+ "|([0-9,\\.\\s]+��([\\s]?��[\\s]?([0-9,\\.\\s]+��)?)?)"; 			
		p = Pattern.compile(pattern);
		m = p.matcher(address);	
		String fixedAddr = "";
		String excluded ="";
		if (m.find()) {
			int end;
			fixedAddr += address.substring(0,m.start(0));
			end = m.end();
			excluded += address.substring(m.start(0), m.end(0));
			//System.out.println(fixedAddr);
			while (m.find()) {
				fixedAddr += address.substring(end,m.start());
				end = m.end();
				excluded += address.substring(m.start(0), m.end(0));
				//System.out.println(fixedAddr);
			}	
			fixedAddr += address.substring(end);
			//System.out.println(fixedAddr);
		} else {
			fixedAddr = address;
		}
		
		
		
		fixedAddr = fixedAddr.trim() +"|"+excluded.trim();
		
		return fixedAddr;
		
		
	}


	private static ArrayList<String> readFileText(String fileName) throws IOException {
		
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		
		ArrayList<String> texts = new ArrayList<String>();
		String line;
		
		
		while ((line=br.readLine()) != null) {
			texts.add(line);
			
		} //while
		br.close();
		
		return texts;
	}
	
	private static String basicParser(String address) {
		
		String pattern;
		Pattern p;
		Matcher m;
		
		//��������
		
		
		//���ȣ, �Ұ�ȣ ����
		//pattern = "(\\[[��-�R0-9��\\s\\.,]+\\])|(\\([��-�R0-9��\\s\\.,]+\\))";
		pattern = "(\\[[^\\[\\]]+\\])|(\\([^\\(\\)]+\\))";
		p = Pattern.compile(pattern);
		m = p.matcher(address);	
		String fixedAddr = "";
		
		if (m.find()) {
			int end;
			fixedAddr += address.substring(0,m.start(0));
			end = m.end();
			//System.out.println(fixedAddr);
			while (m.find()) {
				fixedAddr += address.substring(end,m.start());
				end = m.end();
				//System.out.println(fixedAddr);
			}	
			fixedAddr += address.substring(end);
			//System.out.println(fixedAddr);
		} else {
			fixedAddr = address;
		}
		
		int gwalho1 = fixedAddr.indexOf("(");
		if (gwalho1 !=-1) {
			fixedAddr = fixedAddr.substring(0,gwalho1);	
		}
		
		fixedAddr = fixedAddr.trim();
		
		return fixedAddr;
	}
	
	private static String sidoFixer(String address) {
		
		//�õ� ������ ó���Ѵ�.
		boolean existStandardSido = false;
		boolean existSido3 = false;
		boolean existSido2 = false;
		
		for (String o:sido) {
			if (address.indexOf(o)!=-1) {
				existStandardSido = true;
				existSido3 = true;
				existSido2 = true;
				break;
			}
		}
		
		if (!existStandardSido) {
			String first = address.substring(0, 3);
			String second = address.substring(3);
			for (String[] o:sidoShorten3) {
				if (first.equals(o[0])) {
					address = o[1]+second;
					existSido3 = true;
					existSido2 = true;
					break;
				}				
			}			
		}
		
		if (!existSido3) {
			String first = address.substring(0, 2);
			String second = address.substring(2);
			for (String[] o:sidoShorten2) {
				if (first.equals(o[0])) {
					address = o[1]+second;
					existSido2 = true;
					break;
				}				
			}
		}
		
		
		return address;
	}
	
	private static int getDistance(String longer, String shorter) { //s1�� �� ���ڿ�
	    int longStrLen = longer.length() + 1; 
	    int shortStrLen = shorter.length() + 1; 
	  
	    // �� �ܾ� ��ŭ ũ�Ⱑ ���� ���̹Ƿ�, ���� ��ܾ� �� ���� Cost�� ��� 
	    int[] cost = new int[longStrLen]; 
	    int[] newcost = new int[longStrLen]; 
	  
	    // �ʱ� ����� ���� �� �迭�� ���缭 �ʱ�ȭ ��Ų��. 
	    for (int i = 0; i < longStrLen; i++) { 
	        cost[i] = i; 
	    } 
	  
	    // ª�� �迭�� �ѹ��� ����. 
	    for (int j = 1; j < shortStrLen; j++) { 
	        // �ʱ� Cost�� 1, 2, 3, 4... 
	        newcost[0] = j; 
	  
	       // �� �迭�� �ѹ��� ����. 
	        for (int i = 1; i < longStrLen; i++) { 
	            // ���Ұ� ������ 0, �ƴϸ� 1 
	          int match = 0; 
	          if (longer.charAt(i - 1) != shorter.charAt(j - 1)) { 
	            match = 1; 
	          } 
	           
	            // ��ü, ����, ������ ����� ����Ѵ�. 
	           int replace = cost[i - 1] + match; 
	            int insert = cost[i] + 1; 
	            int delete = newcost[i - 1] + 1; 
	  
	            // ���� ���� ���� ��뿡 �ִ´�. 
	            newcost[i] = Math.min(Math.min(insert, delete), replace); 
	        } 
	  
	        // ���� �ڽ�Ʈ & �� �ڽ�Ʈ ����Ī 
	        int[] temp = cost; 
	        cost = newcost; 
	        newcost = temp; 
	    } 
	  
	    // ���� �������� ���� 
	    return cost[longStrLen - 1]; 
	}

}
