/**TestBase.java
 * 2011/10/24
 * 
 */
package zmonitor.test;

import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.zkoss.monitor.IgnitionFailureException;
import org.zkoss.monitor.Ignitor;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.impl.ThreadLocalTimelineLifecycleManager;
import org.zkoss.monitor.impl.XMLConfigurator;
import org.zkoss.monitor.impl.XmlConfiguratorLoader;
import org.zkoss.monitor.impl.ZMLog;
import org.zkoss.monitor.util.Loader;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TestBase {
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		synchronized(Ignitor.class){
			if(Ignitor.isIgnited())return;
			String packagePath = this.getClass().getPackage().getName().replace('.', '/');
			
			URL url =  findSettingFromPackagePath(packagePath);
			
			if(url==null){
				throw new IgnitionFailureException("cannot find Configuration:["+
						XmlConfiguratorLoader.ZMONITOR_XML+
						"] from every level of package: [" +packagePath+
						"]. Current application context is: "+this.getClass());
			}
			ZMLog.info(">>>>>>>>>>>>>>>load config from: [",url,"]");
			
			XMLConfigurator xmlCofig = 
					XmlConfiguratorLoader.loadFromClassPath(url);
			
			ZMonitorManager manager = new ZMonitorManager();
			manager.setUuid(this.getClass().getName());
			ThreadLocalTimelineLifecycleManager lifecycleMgmt = 
				new ThreadLocalTimelineLifecycleManager();
			
			
			if(!Ignitor.ignite(manager, lifecycleMgmt, xmlCofig)){
				throw new RuntimeException("ZMonitor ignition failed!");
			}
			ZMLog.info(">> Ignit ZMonitor in: ",this.getClass().getCanonicalName());	
		}
	}

	/**
	 * a.b -> a/b/zm.xml
	 * a -> a/zm.xml
	 * 
	 * 
	 * @param packagePath must be "a/b/c", not "a.b.c"
	 */
	private static URL findSettingFromPackagePath(String packagePath){

		URL url = Loader.getResource(packagePath+"/"+XmlConfiguratorLoader.ZMONITOR_XML);
		if(url!=null)return url;
		
		int lastIdx = packagePath.lastIndexOf(".");
		if(lastIdx<=0)
			return Loader.getResource(XmlConfiguratorLoader.ZMONITOR_XML);
		
		String parent = packagePath.substring(0, lastIdx);
		
		return (url==null)? findSettingFromPackagePath(parent) :
			url;
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		//Ignitor.destroy();
	}
}
