package test.eryansky.service.base;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Maps;
import com.lushapp.common.orm.jdbc.JdbcDao;
import com.lushapp.common.utils.io.PropertiesLoader;
import com.lushapp.common.utils.mapper.JsonMapper;
import com.lushapp.modules.sys.entity.Resource;
import com.lushapp.modules.sys.entity.Role;
import com.lushapp.modules.sys.service.CommonManager;
import com.lushapp.modules.sys.service.ResourceManager;
import com.lushapp.modules.sys.service.RoleManager;
/**
 * Account单元测试
 * @author honey.zhao@aliyun.com  
 * @date   2014-8-17 上午9:31:38
 */
public class AccountManagerTest {
	
	@SuppressWarnings("unused")
    private static Properties pro;
	
	private static JdbcDao jdbcDao;
	private static ResourceManager resourceManager;
	private static CommonManager commonManager;
	private static RoleManager roleManager;
	
	
	@BeforeClass
	public static void init() throws Exception{
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
		jdbcDao = (JdbcDao)context.getBean("jdbcDao");
		resourceManager = (ResourceManager)context.getBean("resourceManager");
		commonManager = (CommonManager)context.getBean("commonManager");
		roleManager = (RoleManager)context.getBean("roleManager");
		
		pro = new PropertiesLoader("/appconfig.properties").getProperties();
	}
	
	@Test
    public void test2(){
	    Map<String, String> map = Maps.newHashMap();
	    map.put("name", "%");
	    System.out.println(jdbcDao.queryForList("select * from t_base_resource where name like :name ",  map));
    }
	
	
	@Test
    public void page(){
		String sql = "select * from T_SYS_USER";
		List list1 = jdbcDao.queryForList(sql, 1, 20,null);
		System.out.println(JsonMapper.nonEmptyMapper().toJson(list1));
    }
	
	@Test
    public void assertt(){
		System.out.println(commonManager.getIdByProperty("Resource", "name", "菜单管理"));
		System.out.println(commonManager.getIdByTFO("T_SYS_MENU", "NAME", "菜 单管理"));
    }
	@Test
    public void validator(){
		try {
			resourceManager.save(new Resource());
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			javax.validation.ConstraintViolationException ce = (javax.validation.ConstraintViolationException) e;
			Set<ConstraintViolation<?>> set =  ce.getConstraintViolations();
			Iterator<?> iterator = set.iterator();
			int i = -1;
			while(iterator.hasNext()){
				ConstraintViolation<?> c = (ConstraintViolation<?>) iterator.next();
				sb.append(c.getMessage());
				i++;
				if (i < set.size() - 1) {
					sb.append(",");
				}else{
					sb.append(".");
				}
			}
			System.out.println(sb.toString());
			e.printStackTrace();
		}
	}
	
	@Test
    public void bf(){
		Thread thread1 = new Thread(new Runnable() {
			
			public void run() {
				for(int i=0;i<1000;i++){
					try {
						Resource m = new Resource();
						m.setName(i+"");
						resourceManager.save(m);
						System.out.println(1+ " "+i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		Thread thread2 = new Thread(new Runnable() {
			
			public void run() {
				for(int i=0;i<1000;i++){
					try {
						Role r = new Role();
						r.setName(i+"");
						roleManager.save(r);
						System.out.println(2+ " "+i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread1.start();
		thread2.start();
    }

    @Test
    public void getAppResourcesByUserId(){

    }
	

}

