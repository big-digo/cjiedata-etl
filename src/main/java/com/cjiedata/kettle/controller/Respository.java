package com.cjiedata.kettle.controller;
import com.cjiedata.kettle.App;
import org.apache.commons.lang.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.*;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import entity.Result;
import enums.StatusCode;

import java.io.IOException;
import java.util.List;

@RestController
@EnableAutoConfiguration
@RequestMapping("/repo")
public class Respository {
    KettleDatabaseRepository repository;
    @RequestMapping("/list")
    Result list() throws KettleException {
        if (repository == null) {
            repository = RepositoryCon();
        }
        String msg = list(repository, "lilong");
        return new Result(true, StatusCode.SUCCESS.getCode(), msg);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{}")
    protected void remove(@RequestParam String path) throws KettleException, IOException {
        RepositoriesMeta input = new RepositoriesMeta();
        input.readData();

        RepositoryMeta previous = input.searchRepository(path);
        input.removeRepository(input.indexOfRepository(previous));
        input.writeData();

        JsonUtils.success("操作成功！");
    }

    private KettleDatabaseRepository RepositoryCon() throws KettleException {
        //初始化
        KettleEnvironment.init();
        //数据库连接元对象
        DatabaseMeta dataMeta = new DatabaseMeta("kettle-repo", "MariaDB", "Native(JDBC)", "localhost", "kettle-repo", "3306", "root", "123456");
        //数据库形式的资源库元对象
        KettleDatabaseRepositoryMeta repInfo = new KettleDatabaseRepositoryMeta();
        repInfo.setConnection(dataMeta);
        //数据库形式的资源库对象
        KettleDatabaseRepository rep = new KettleDatabaseRepository();
        //用资源库元对象初始化资源库对象
        rep.init(repInfo);
        //连接到资源库
        rep.connect("admin", "admin");//默认的连接资源库的用户名和密码
        if(rep.isConnected()){
            System.out.println("连接成功");
            return rep;
        }else{
            System.out.println("连接失败");
            return null;
        }
    }

    private String list(KettleDatabaseRepository rep, String user) {
        try {
            RepositoryDirectoryInterface dir = rep.findDirectory("/" + user);//根据指定的字符串路径 找到目录
            JSONObject info = new JSONObject();
            List<RepositoryElementMetaInterface> objs = rep.getJobAndTransformationObjects(dir.getObjectId(), false);

            JSONArray jsonArray = new JSONArray();
            for (RepositoryElementMetaInterface obj : objs) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", obj.getObjectType());
                jsonObject.put("modifiedDate", obj.getModifiedDate());
                jsonObject.put("description", obj.getDescription());
                jsonObject.put("name", obj.getName());
                jsonObject.put("path", "/" + user);

                jsonArray.add(jsonObject);
            }
            return jsonArray.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
