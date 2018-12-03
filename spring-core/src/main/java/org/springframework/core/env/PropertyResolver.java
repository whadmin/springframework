/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.core.env;

/**
 *
 * @see PropertySource  表示属性源，我们可以用它在不不同的实现在内存中表示一组key-value结构的数据
 * 如MapPropertySource,PropertiesPropertySource,ResourcePropertySource
 * @see PropertySources 使用CopyOnWriteArrayList用来管理多个PropertySource
 * @see PropertyResolver 表示属性解析器,它内部保存一个PropertySources
 * 可以解析属性key对应value值。和PropertySource不同的是它之所是解析而非获取因为它具体如下功能：

 * 1 可以对获取key对应value值如果是占位符，可进行多次解析
 *   key1:Hello {key2}
 *   key2:world
 *   getProperty(String key1)== Hello world
 * 2 可以对一段文本中属性站位符做替换
 *   key1:world
 *   resolvePlaceholders("hellow key1")==hellow world
 * 3 可以对解析后的属性值做类型转换
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see Environment
 * @see PropertySourcesPropertyResolver
 */
public interface PropertyResolver {

	/**
	 * 返回给定的属性key是否可用于解析
	 */
	boolean containsProperty(String key);

	/**
	 * 返回与给定属性key关联的属性值value,如果属性key无法解析，则为{@code null}。
	 * @param key 要解析的属性名称
	 * @see #getProperty(String, String)
	 * @see #getProperty(String, Class)
	 * @see #getRequiredProperty(String)
	 */
	String getProperty(String key);

	/**
	 * 返回与给定属性key关联的属性值value，如果无法解析键，则返回 {@code defaultValue}。
	 * @param key 要解析的属性名称
	 * @param defaultValue 如果未找到任何值，则返回默认值
	 * @see #getRequiredProperty(String)
	 * @see #getProperty(String, Class)
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * 返回与给定属性key关联的属性值value,如果属性key无法解析，则为{@code null}。
	 * @param key 要解析的属性名称
	 * @param targetType 预期的属性值类型
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType);

	/**
	 * 返回与给定属性key关联的属性值value，如果无法解析键，则返回 {@code defaultValue}。
	 * @param key 要解析的属性名称
	 * @param targetType 预期的属性值类型
	 * @param defaultValue 如果未找到任何值，则返回默认值
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	/**
	 * 返回与给定键关联的属性值 {@link #getProperty(String)} 不同在于当返回value为null时抛出异常
	 * @throws IllegalStateException if the key cannot be resolved
	 * @see #getRequiredProperty(String, Class)
	 */
	String getRequiredProperty(String key) throws IllegalStateException;

	/**
	 * 返回与给定键关联的属性值 {@link #getProperty(String, Class<T>)} 不同在于当返回value为null时抛出异常
	 * @throws IllegalStateException if the given key cannot be resolved
	 */
	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

	/**
	 * 解析给定文本中的$ {...}占位符，将其替换为{@link #getProperty}解析的相应属性值。, 无默认值的无法解决的占位符将被忽略并传递不变。
	 * @param text 要解析的字符串
	 * @return the 已解析的字符串（从不{@code null}）
	 * @throws IllegalArgumentException if given text is {@code null}
	 * @see #resolveRequiredPlaceholders
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String)
	 */
	String resolvePlaceholders(String text);

	/**
	 * 解析给定文本中的$ {...}占位符，将其替换为{@link #getProperty}解析的相应属性值。, 具有无默认值的无法解析的占位符将导致抛出IllegalArgumentException。
	 * @return t已解析的字符串（从不{@code null}）
	 * @throws IllegalArgumentException if given text is {@code null}
	 * or if any placeholders are unresolvable
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String, boolean)
	 */
	String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

	/**
	 * 此方法以及过期，使用{@link #getProperty(String, Class<T>)}代替。
	 */
	@Deprecated
	<T> Class<T> getPropertyAsClass(String key, Class<T> targetType);

}
