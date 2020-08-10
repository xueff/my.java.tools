import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * 文件追加内容
 * @version 2011-3-10 下午10:49:41
 */
public class JSONFileUtils {


    public static void main(String[] args)   {
        List<Dto> dtos = readFile(new File("C:\\Users\\ffxue\\hbase.json"));
        dtos.forEach(it->{
            appendFile(it.getDbName()+","+it.getFamily()+","+it.getTableName()+","+it.getColumn()+","+it.getValues()+"\n","C:\\Users\\ffxue\\hbase.csv");
        });

    }
//    public static void main(String[] args)   {
//        readFile(new File("C:\\Users\\ffxue\\keeplive.text"));
//
//
//    }

    /**
     *
     * @param cfg
     * @return
     */
    public static  void readFile2(File cfg) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(cfg), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    JsonObject json = new JsonObject(line);
                    int cpu = json.getInteger("cpu");
                    int men = json.getInteger("men");
                    appendFile(cpu+","+men+"\n","C:\\Users\\ffxue\\heart.csv");

                }catch (Exception e){
                }
            }
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            System.out.println("读取文件失败");
        }

    }

    /**
     *
     * @param cfg
     * @return
     */
    public static  List<Dto> readFile(File cfg) {
        List<Dto> dtos = new ArrayList<>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(cfg), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    JsonObject json = new JsonObject(line);
                    JsonArray array = json.getJsonArray("tableList");
                    array.forEach(it->{
                        JsonObject table = (JsonObject) it;
                        String tableName = table.getString("name");
                        JsonArray columns = table.getJsonArray("columns");
                        columns.forEach(it2->{
                            JsonObject column = (JsonObject) it2;
                            String columnFamily = column.getString("columnFamily");
                            String name = column.getString("name");
                            JsonArray ruleInfo = column.getJsonArray("ruleInfo");
                            Dto dto = new Dto();
                            dto.setDbName("default");
                            dto.setTableName(tableName);
                            dto.setFamily(columnFamily);
                            dto.setColumn(name);
                            StringBuffer sb = new StringBuffer();
                            for(int i=0;i<ruleInfo.size();i++){
                                int count = ruleInfo.getJsonObject(i).getInteger("hitCount");
                                String ruleName = ruleInfo.getJsonObject(i).getString("ruleName");
                                sb.append(ruleName).append(",").append(count).append(",");
                            }
                            dto.setValues(sb.length()>0?sb.substring(0,sb.length()-1):"");
//                            if(!dtos.contains(dto)){
//                                dtos.add(dto);
//                            }
                        });
                    });
                }catch (Exception e){
                }
            }
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            System.out.println("读取文件失败");
        }
        return dtos;
    }


    /**
     * 向文件追加内容
     */
    public static void appendFile(String content,String fileName){
        if(StringUtils.isEmpty(content)){
            return;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


class Dto{
    private String dbName="";
    private String tableName="";
    private String family="";
    private String column="";
    private String values="";

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dto dto = (Dto) o;
        return Objects.equals(dbName, dto.dbName) &&
                Objects.equals(tableName, dto.tableName) &&
                Objects.equals(family, dto.family) &&
                Objects.equals(column, dto.column) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbName, tableName, family, column, values);
    }
}
