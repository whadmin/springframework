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
	 * 为bean样式配置创建一个新的ClassPathXmlApplicationContext。
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public ClassPathXmlApplicationContext() {
	}

	/**
	 * 为bean样式配置创建一个新的ClassPathXmlApplicationContext。
	 * @param parent 表示设置一个父容器
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public ClassPathXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}

	/**
	 * 创建一个新的ClassPathXmlApplicationContext，从给定的XML文件加载定义*并自动刷新上下文。
	 * @param configLocation 资源位置
	 * @throws BeansException 如果context创建失败抛出
	 */
	public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}

	/**
	 * 创建一个新的ClassPathXmlApplicationContext，从给定的XML文件加载定义*并自动刷新上下文。
	 * @param configLocations 资源位置数组
	 * @throws BeansException 如果context创建失败抛出
	 */
	public ClassPathXmlApplicationContext(String... configLocations) throws BeansException {
		this(configLocations, true, null);
	}

	/**
	 * 使用给定父容器创建新的ClassPathXmlApplicationContext，*从给定的XML文件加载定义并自动*刷新上下文。
	 * @param configLocations 资源位置数组
	 * @param parent 表示设置一个父容器
	 * @throws BeansException 如果context创建失败抛出
	 */
	public ClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
		this(configLocations, true, parent);
	}

	/**
	 * 创建一个新的ClassPathXmlApplicationContext，从给定的XML文件加载定义* *。
	 * @param configLocations 资源位置数组
	 * @param refresh 是否自动刷新上下文，
	 * 加载所有bean定义并创建所有单例。 , *或者，在进一步配置上下文后手动调用刷新.
	 * @throws BeansException 如果context创建失败抛出
	 * @see #refresh()
	 */
	public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		this(configLocations, refresh, null);
	}

	/**
	 * 使用给定父级创建新的ClassPathXmlApplicationContext，*从给定的XML文件加载定义。
	 * @param configLocations 资源位置数组
	 * @param refresh 是否刷新上下文,
	 * 加载所有bean定义并创建所有单例。 , *或者，在进一步配置上下文后手动调用刷新.
	 * @param parent 设置父容器
	 * @throws BeansException 如果context创建失败抛出
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
	 * 创建一个新的ClassPathXmlApplicationContext，从给定的XML文件加载定义*并自动刷新上下文。 , * <
	 * p>这是一种相对于给定类加载类路径资源的便捷方法。, 要获得完全的灵活性，请考虑将GenericApplicationContext *与XmlBeanDefinitionReader和ClassPathResource参数一起使用。
	 * @param path 类路径中的相对（或绝对）路径
	 * @param clazz 用于加载资源的类（给定路径的基础）
	 * @throws BeansException 如果context创建失败抛出
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public ClassPathXmlApplicationContext(String path, Class<?> clazz) throws BeansException {
		this(new String[] {path}, clazz);
	}

	/**
	 * 创建一个新的ClassPathXmlApplicationContext，从给定的XML文件加载定义*并自动刷新上下文。
	 * @param paths 类路径中的相对（或绝对）路径的数组
	 * @param clazz 用于加载资源的类（给定路径的基础）
	 * @throws BeansException 如果context创建失败抛出
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz) throws BeansException {
		this(paths, clazz, null);
	}

	/**
	 * 使用给定父级创建新的ClassPathXmlApplicationContext，*从给定的XML文件加载定义并自动*刷新上下文。
	 * @param paths 类路径中的相对（或绝对）路径的数组
	 * @param clazz 用于加载资源的类（给定路径的基础）
	 * @param parent 设置父容器
	 * @throws BeansException 如果context创建失败抛出
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
