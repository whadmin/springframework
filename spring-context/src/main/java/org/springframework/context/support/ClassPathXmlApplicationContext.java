/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Standalone XML application context, taking the context definition files
 * from the class path, interpreting plain paths as class path resource names
 * that include the package path (e.g. "mypackage/myresource.txt"). Useful for
 * test harnesses as well as for application contexts embedded within JARs.
 *
 * <p>The config location defaults can be overridden via {@link #getConfigLocations},
 * Config locations can either denote concrete files like "/myfiles/context.xml"
 * or Ant-style patterns like "/myfiles/*-context.xml" (see the
 * {@link org.springframework.util.AntPathMatcher} javadoc for pattern details).
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in earlier loaded files. This can be leveraged to
 * deliberately override certain bean definitions via an extra XML file.
 *
 * <p><b>This is a simple, one-stop shop convenience ApplicationContext.
 * Consider using the {@link GenericApplicationContext} class in combination
 * with an {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}
 * for more flexible context setup.</b>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getResource
 * @see #getResourceByPath
 * @see GenericApplicationContext
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

	private Resource[] configResources;


	/**
	 * Ϊbean��ʽ���ô���һ���µ�ClassPathXmlApplicationContext��
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public ClassPathXmlApplicationContext() {
	}

	/**
	 * Ϊbean��ʽ���ô���һ���µ�ClassPathXmlApplicationContext��
	 * @param parent ��ʾ����һ��������
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public ClassPathXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}

	/**
	 * ����һ���µ�ClassPathXmlApplicationContext���Ӹ�����XML�ļ����ض���*���Զ�ˢ�������ġ�
	 * @param configLocation ��Դλ��
	 * @throws BeansException ���context����ʧ���׳�
	 */
	public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}

	/**
	 * ����һ���µ�ClassPathXmlApplicationContext���Ӹ�����XML�ļ����ض���*���Զ�ˢ�������ġ�
	 * @param configLocations ��Դλ������
	 * @throws BeansException ���context����ʧ���׳�
	 */
	public ClassPathXmlApplicationContext(String... configLocations) throws BeansException {
		this(configLocations, true, null);
	}

	/**
	 * ʹ�ø��������������µ�ClassPathXmlApplicationContext��*�Ӹ�����XML�ļ����ض��岢�Զ�*ˢ�������ġ�
	 * @param configLocations ��Դλ������
	 * @param parent ��ʾ����һ��������
	 * @throws BeansException ���context����ʧ���׳�
	 */
	public ClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
		this(configLocations, true, parent);
	}

	/**
	 * ����һ���µ�ClassPathXmlApplicationContext���Ӹ�����XML�ļ����ض���* *��
	 * @param configLocations ��Դλ������
	 * @param refresh �Ƿ��Զ�ˢ�������ģ�
	 * ��������bean���岢�������е����� , *���ߣ��ڽ�һ�����������ĺ��ֶ�����ˢ��.
	 * @throws BeansException ���context����ʧ���׳�
	 * @see #refresh()
	 */
	public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		this(configLocations, refresh, null);
	}

	/**
	 * ʹ�ø������������µ�ClassPathXmlApplicationContext��*�Ӹ�����XML�ļ����ض��塣
	 * @param configLocations ��Դλ������
	 * @param refresh �Ƿ�ˢ��������,
	 * ��������bean���岢�������е����� , *���ߣ��ڽ�һ�����������ĺ��ֶ�����ˢ��.
	 * @param parent ���ø�����
	 * @throws BeansException ���context����ʧ���׳�
	 * @see #refresh()
	 */
	public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
			throws BeansException {

		super(parent);
		setConfigLocations(configLocations);
		if (refresh) {
			refresh();
		}
	}


	/**
	 * ����һ���µ�ClassPathXmlApplicationContext���Ӹ�����XML�ļ����ض���*���Զ�ˢ�������ġ� , * <
	 * p>����һ������ڸ����������·����Դ�ı�ݷ�����, Ҫ�����ȫ������ԣ��뿼�ǽ�GenericApplicationContext *��XmlBeanDefinitionReader��ClassPathResource����һ��ʹ�á�
	 * @param path ��·���е���ԣ�����ԣ�·��
	 * @param clazz ���ڼ�����Դ���ࣨ����·���Ļ�����
	 * @throws BeansException ���context����ʧ���׳�
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public ClassPathXmlApplicationContext(String path, Class<?> clazz) throws BeansException {
		this(new String[] {path}, clazz);
	}

	/**
	 * ����һ���µ�ClassPathXmlApplicationContext���Ӹ�����XML�ļ����ض���*���Զ�ˢ�������ġ�
	 * @param paths ��·���е���ԣ�����ԣ�·��������
	 * @param clazz ���ڼ�����Դ���ࣨ����·���Ļ�����
	 * @throws BeansException ���context����ʧ���׳�
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz) throws BeansException {
		this(paths, clazz, null);
	}

	/**
	 * ʹ�ø������������µ�ClassPathXmlApplicationContext��*�Ӹ�����XML�ļ����ض��岢�Զ�*ˢ�������ġ�
	 * @param paths ��·���е���ԣ�����ԣ�·��������
	 * @param clazz ���ڼ�����Դ���ࣨ����·���Ļ�����
	 * @param parent ���ø�����
	 * @throws BeansException ���context����ʧ���׳�
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz, ApplicationContext parent)
			throws BeansException {

		super(parent);
		Assert.notNull(paths, "Path array must not be null");
		Assert.notNull(clazz, "Class argument must not be null");
		this.configResources = new Resource[paths.length];
		for (int i = 0; i < paths.length; i++) {
			this.configResources[i] = new ClassPathResource(paths[i], clazz);
		}
		refresh();
	}


	@Override
	protected Resource[] getConfigResources() {
		return this.configResources;
	}

}
