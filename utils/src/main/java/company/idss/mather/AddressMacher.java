package company.idss.mather;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressMacher {
    /**
     * //      Pattern p=Pattern.compile("([a-z]+)(\\d+)");
     *         //Matcher m=p.matcher("aaa2223bb");
     *         //m.find();   //匹配aaa2223
     *         //m.groupCount();   //返回2,因为有2组
     *         //m.start(1);   //返回0 返回第一组匹配到的子字符串在字符串中的索引号
     *         //m.start(2);   //返回3
     *         //m.end(1);   //返回3 返回第一组匹配到的子字符串的最后一个字符在字符串中的索引位置.
     *         //m.end(2);   //返回7
     *         //m.group(1);   //返回aaa,返回第一组匹配到的子字符串
     *         //m.group(2);   //返回2223,返回第二组匹配到的子字符串
     */
    @Test
    public void findAddress(){
        Map<String,String> pcMAp = new HashMap<>();
        pcMAp.put("苏州","");
        pcMAp.put("江苏","");
        pcMAp.put("北京","");
        pcMAp.put("天津","");
        pcMAp.put("重庆","");
        int total = 0;
        int percent = 1;
        String ad = "中华人民共和国北京市天津11重庆江苏苏州市工业园区";
//        String ad = "宁夏回族自治区江苏省苏州市工业园区第三街道峰湖路28号创业产业园A2号楼12楼28单元1201室";
//        String ad = "几天29号，明天30号，是的";
        //房子
        String xiangxi = "[\\d]{1,4}(幢|号楼|楼|栋|单元|室)+?";
        Pattern p = Pattern.compile(xiangxi);
        Matcher m=p.matcher(ad);
        String next = "";
        int i = 0;
        while (m.find()) {
            int end = m.end();
            System.out.println(ad.substring(i,end));
            if(i ==0){
                next = ad.substring(i,m.start());
            }
            percent+=percent;
            i =end;
        }
        total = percent;
        if(total>=3){
            total = 3;
        }

        percent = 0;
        //省市
        String p_c = "(省|自治区|市|地区|州|盟)";
        p = Pattern.compile(p_c);
        next = next.equals("")?ad:next;
        m=p.matcher(next.equals("")?ad:next);
        i = 0;
        while (m.find()) {
            int end = m.end();
            String match = m.group();
            if(m.group().equals("州")){
                if(next.charAt(end) == '市'){
                    end+=1;
                    match = "市";
                }
            }
            String items = next.substring(i, end);
            if(!pcMAp.containsKey(items)&& StringUtils.isNotEmpty(items)){
                String split = next.substring(i, end-match.length());
                //split拆分check  TODO
                int flag = split.length();
                for(int j=flag-2;j>=0;j--){
                    String city = split.substring(j,flag);
                    if(pcMAp.containsKey(city)){
                        percent +=3;
                        flag = j;
                        j = j-1;
                    }

                }
            }else {
                percent +=3;
            }
            System.out.println(next.substring(i, end));
            if(percent ==0) {
            }
            i =end;

        }

        next = next.substring(i);

        total+=percent;
        percent = 1;
        if(total>=7){
            System.out.println("return:"+total);
        }

        int add = 1;
        if(total<=2){
            add = 2;
        }else if(total>=3){
            add=3;
        }
        //地级市区县镇
        String q_x = "(市|县|区|镇|乡)";
        p = Pattern.compile(q_x);
        next = next.equals("")?ad:next;
        m=p.matcher(next);
        i = 0;
        while (m.find()) {
            int end = m.end();
            System.out.println(next.substring(i,end));
            i =end;
            percent+=add;
        }
        next = next.substring(i);
        if(total>=6){
            System.out.println("return:"+total);
        }

        i=0;
        String quyu = "(街道|路|街)\\d{1,4}号|(街道|路|街|小区)";
        p = Pattern.compile(quyu);
        next = next.equals("")?ad:next;
        m=p.matcher(next);
        while (m.find()) {
            int end = m.end();
            System.out.println(next.substring(i,end));
            i =end;
            total +=3;
        }
        if(total>=7){
            System.out.println("return:"+total);
        }
        System.out.println("percent:"+total);

    }
}
