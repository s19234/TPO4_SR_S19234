/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Tools {

    public static Options createOptionsFromYaml(String fileName) throws IOException {
        Yaml yaml = new Yaml();
        FileReader fileReader = new FileReader(new File(fileName));
        Map<String, Object> stringObjectMap = yaml.load(fileReader);
        String host = (String)stringObjectMap.get("host");
        int port = (int)stringObjectMap.get("port");
        boolean isConcur = (boolean)stringObjectMap.get("concurMode");
        boolean isShow = (boolean)stringObjectMap.get("showSendRes");
        Map<String, List<String>> stringListMap = (Map<String, List<String>>)stringObjectMap.get("clientsMap");
        return new Options(host, port, isConcur, isShow, stringListMap);
    }
}
