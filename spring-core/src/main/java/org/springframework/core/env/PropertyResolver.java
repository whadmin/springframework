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
 * @see PropertySource  ��ʾ����Դ�����ǿ��������ڲ���ͬ��ʵ�����ڴ��б�ʾһ��key-value�ṹ������
 * ��MapPropertySource,PropertiesPropertySource,ResourcePropertySource
 * @see PropertySources ʹ��CopyOnWriteArrayList����������PropertySource
 * @see PropertyResolver ��ʾ���Խ�����,���ڲ�����һ��PropertySources
 * ���Խ�������key��Ӧvalueֵ����PropertySource��ͬ������֮���ǽ������ǻ�ȡ��Ϊ���������¹��ܣ�

 * 1 ���ԶԻ�ȡkey��Ӧvalueֵ�����ռλ�����ɽ��ж�ν���
 *   key1:Hello {key2}
 *   key2:world
 *   getProperty(String key1)== Hello world
 * 2 ���Զ�һ���ı�������վλ�����滻
 *   key1:world
 *   resolvePlaceholders("hellow key1")==hellow world
 * 3 ���ԶԽ����������ֵ������ת��
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see Environment
 * @see PropertySourcesPropertyResolver
 */
public interface PropertyResolver {

	/**
	 * ���ظ���������key�Ƿ�����ڽ���
	 */
	boolean containsProperty(String key);

	/**
	 * �������������key����������ֵvalue,�������key�޷���������Ϊ{@code null}��
	 * @param key Ҫ��������������
	 * @see #getProperty(String, String)
	 * @see #getProperty(String, Class)
	 * @see #getRequiredProperty(String)
	 */
	String getProperty(String key);

	/**
	 * �������������key����������ֵvalue������޷����������򷵻� {@code defaultValue}��
	 * @param key Ҫ��������������
	 * @param defaultValue ���δ�ҵ��κ�ֵ���򷵻�Ĭ��ֵ
	 * @see #getRequiredProperty(String)
	 * @see #getProperty(String, Class)
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * �������������key����������ֵvalue,�������key�޷���������Ϊ{@code null}��
	 * @param key Ҫ��������������
	 * @param targetType Ԥ�ڵ�����ֵ����
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType);

	/**
	 * �������������key����������ֵvalue������޷����������򷵻� {@code defaultValue}��
	 * @param key Ҫ��������������
	 * @param targetType Ԥ�ڵ�����ֵ����
	 * @param defaultValue ���δ�ҵ��κ�ֵ���򷵻�Ĭ��ֵ
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	/**
	 * ���������������������ֵ {@link #getProperty(String)} ��ͬ���ڵ�����valueΪnullʱ�׳��쳣
	 * @throws IllegalStateException if the key cannot be resolved
	 * @see #getRequiredProperty(String, Class)
	 */
	String getRequiredProperty(String key) throws IllegalStateException;

	/**
	 * ���������������������ֵ {@link #getProperty(String, Class<T>)} ��ͬ���ڵ�����valueΪnullʱ�׳��쳣
	 * @throws IllegalStateException if the given key cannot be resolved
	 */
	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

	/**
	 * ���������ı��е�$ {...}ռλ���������滻Ϊ{@link #getProperty}��������Ӧ����ֵ��, ��Ĭ��ֵ���޷������ռλ���������Բ����ݲ��䡣
	 * @param text Ҫ�������ַ���
	 * @return the �ѽ������ַ������Ӳ�{@code null}��
	 * @throws IllegalArgumentException if given text is {@code null}
	 * @see #resolveRequiredPlaceholders
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String)
	 */
	String resolvePlaceholders(String text);

	/**
	 * ���������ı��е�$ {...}ռλ���������滻Ϊ{@link #getProperty}��������Ӧ����ֵ��, ������Ĭ��ֵ���޷�������ռλ���������׳�IllegalArgumentException��
	 * @return t�ѽ������ַ������Ӳ�{@code null}��
	 * @throws IllegalArgumentException if given text is {@code null}
	 * or if any placeholders are unresolvable
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String, boolean)
	 */
	String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

	/**
	 * �˷����Լ����ڣ�ʹ��{@link #getProperty(String, Class<T>)}���档
	 */
	@Deprecated
	<T> Class<T> getPropertyAsClass(String key, Class<T> targetType);

}
