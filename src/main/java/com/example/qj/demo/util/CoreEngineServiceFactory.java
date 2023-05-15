package com.example.qj.demo.util;

import com.example.qj.demo.entity.MyIdentityProvider;
import com.example.qj.demo.entity.MyUserGroupCallback;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.utils.KieServiceConfigurator;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.conf.*;
import org.kie.scanner.KieMavenRepository;
import org.kie.test.util.db.DataSourceFactory;
import org.kie.test.util.db.PoolingDataSourceWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import static com.example.qj.demo.constant.ProcessConstant.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class CoreEngineServiceFactory {

    public static PoolingDataSourceWrapper ds;
    public static KieServiceConfigurator serviceConfigurator;
    public static DeploymentUnit deploymentUnit;

    /**
     * jbpm的流程初始化操作
     */
    public static void init() throws Exception {
        //1.配置持久化数据源头
        ds=buildDatasource();
        //2.配置jbpm核型引擎service
        serviceConfigurator=configureServices();
        //3.创建服务单元
        deploymentUnit=createDeployUnit(ARTIFACT_ID,GROUP_ID,VERSION);
    }

    /**
     * 配置数据源
     */
    public static PoolingDataSourceWrapper buildDatasource(){
        Properties driverProperties = new Properties();
        driverProperties.put("user", "sa");
        driverProperties.put("password", "sasa");
        driverProperties.put("url", "jdbc:h2:mem:mydb");
        driverProperties.put("driverClassName", "org.h2.Driver");
        driverProperties.put("className", "org.h2.jdbcx.JdbcDataSource");

        PoolingDataSourceWrapper ds = DataSourceFactory.setupPoolingDataSource("jdbc/jbpm", driverProperties);
        return ds;
    }


    /**
     * 获取部署服务的对象，一系列的配置操作
     * @return
     */
    public static KieServiceConfigurator configureServices(){

        //1.简单初始化
        KieServiceConfigurator serviceConfigurator=(KieServiceConfigurator) ServiceLoader.load(KieServiceConfigurator.class).iterator().next();

        //2.实现IdentityProvider和UserGroupCallback接口
        MyIdentityProvider identityProvider = new MyIdentityProvider();
        MyUserGroupCallback userGroupCallback = new MyUserGroupCallback();

        //3.增加持久化配置和事务
        serviceConfigurator.configureServices("org.jbpm.domain", identityProvider, userGroupCallback);
        return serviceConfigurator;
    }

    /**
     * 创建基本的部署流程单元服务
     * @param groupId
     * @param artifactid
     * @param version
     * @return
     * @throws Exception
     */
    public static DeploymentUnit createDeployUnit(String groupId, String artifactid, String version) throws Exception {
        List<String> list = new ArrayList<>();
        list.add("rule/hello.bpmn");


        if (list!= null && !list.isEmpty()) {

            //1.创建ReleaseId对象
            KieServices ks = KieServices.Factory.get();
            ReleaseId releaseId = ks.newReleaseId(groupId, artifactid, version);

            //2.创建流程模型kjar并写文件
            KieModuleModel kproj = ks.newKieModuleModel();
            KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*").setEqualsBehavior(EqualityBehaviorOption.EQUALITY).setEventProcessingMode(EventProcessingOption.STREAM);
            KieSessionModel ksessionModel = kieBaseModel1.newKieSessionModel("ksession-test");
            ksessionModel.setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime"));
            ksessionModel.newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
            ksessionModel.newWorkItemHandlerModel("Service Task", "new org.jbpm.bpmn2.handler.ServiceTaskHandler(\"name\")");
            KieFileSystem kfs = ks.newKieFileSystem();
            kfs.writeKModuleXML(kproj.toXML());

            //写xml文件
            List<ReleaseId> reList=new ArrayList<>();
            String reContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" + "  <modelVersion>4.0.0</modelVersion>\n" + "\n" + "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" + "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" + "  <version>" + releaseId.getVersion() + "</version>\n" + "\n";
            if (reList != null && reList.size() > 0) {
                reContent += "<dependencies>\n";
                for (ReleaseId dep : reList) {
                    reContent += "<dependency>\n";
                    reContent += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                    reContent += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                    reContent += "  <version>" + dep.getVersion() + "</version>\n";
                    reContent += "</dependency>\n";
                }
                reContent += "</dependencies>\n";
            }
            reContent += "</project>";
            kfs.writePomXML(reContent);


            Map<String, String> map = new HashMap<String, String>();
            DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
            if (customDescriptor != null) {
                map.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());
            }

            for (String resource : list) {
                kfs.write("src/main/resources/" + resource, ResourceFactory.newClassPathResource(resource));
            }
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    kfs.write(entry.getKey(), ResourceFactory.newByteArrayResource(entry.getValue().getBytes()));
                }
            }
            KieBuilder kieBuilder = ks.newKieBuilder(kfs);
            InternalKieModule kJar1=(InternalKieModule) kieBuilder.getKieModule();


            //2.1 拼凑pom文件内容并写入文件
            File file = new File("target/kmodule", "pom.xml");
            file.getParentFile().mkdir();
            List<ReleaseId> dependencies=new ArrayList<>();
            String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" + "  <modelVersion>4.0.0</modelVersion>\n" + "\n" + "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" + "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" + "  <version>" + releaseId.getVersion() + "</version>\n" + "\n";
            if (dependencies != null && dependencies.size() > 0) {
                content += "<dependencies>\n";
                for (ReleaseId dep : dependencies) {
                    content += "<dependency>\n";
                    content += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                    content += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                    content += "  <version>" + dep.getVersion() + "</version>\n";
                    content += "</dependency>\n";
                }
                content += "</dependencies>\n";
            }
            content += "</project>";
            FileOutputStream fs = new FileOutputStream(file);
            fs.write(content.getBytes());
            fs.close();

            //3.将资源写入仓库
            KieMavenRepository repository = getKieMavenRepository();
            repository.deployArtifact(releaseId, kJar1, file);

            //4.创建并部署服务单元
            KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(groupId, artifactid, version);
            return deploymentUnit;
        }
        return null;
    }


}
