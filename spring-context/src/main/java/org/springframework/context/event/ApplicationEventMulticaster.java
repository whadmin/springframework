/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;

/**
 * �ɿ��Թ������* {@link ApplicationListener}�������䷢���¼��Ķ���ʵ�ֵĽӿڡ�
 *
 * {@link org.springframework.context.ApplicationEventPublisher}��ͨ����
 * Spring {@link org.springframework.context.ApplicationContext}������ʹ��
 * ApplicationEventMulticaster��Ϊʵ�ʷ����¼���ί�С�
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 */
public interface ApplicationEventMulticaster {

	/**
	 * ���һ����������֪ͨ�����¼��� ,
	 * @param listener ���������������
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * ���һ��������bean��֪ͨ�����¼���
	 * @param listenerBeanName Ҫ��ӵ�������bean������
	 */
	void addApplicationListenerBean(String listenerBeanName);

	/**
	 * ���������б���ɾ����������
	 * @param listener Ҫɾ���ļ�����
	 */
	void removeApplicationListener(ApplicationListener<?> listener);

	/**
	 * ���������б���ɾ��������bean��
	 * @param listenerBeanName Ҫɾ����������bean������
	 */
	void removeApplicationListenerBean(String listenerBeanName);

	/**
	 * ɾ���ڴ˶ಥ������ע��������������� ,
	 * <p>��ɾ������֮�󣬶ಥ�������¼�֪ͨ�в�ִ���κβ���
	 * ֱ��ע���µ���������
	 */
	void removeAllListeners();

	/**
	 * ��������Ӧ�ó����¼��ಥ���ʵ����������� ,
	 * <p>������ܣ��뿼��ʹ��{@link #multicastEvent��ApplicationEvent��ResolvableType��}
	 *����Ϊ��Ϊ���ڷ��͵��¼��ṩ�˸��õ�֧�֡�
	 * @param event the event to multicast
	 */
	void multicastEvent(ApplicationEvent event);

	/**
	 * ��������Ӧ�ó����¼��ಥ���ʵ����������� ,
	 * <p>���{@code eventType}Ϊ{@code null}�������{@code event}ʵ������*��Ĭ�����͡�
	 * @param event the event to multicast
	 * @param eventType the type of event (can be null)
	 * @since 4.2
	 */
	void multicastEvent(ApplicationEvent event, ResolvableType eventType);

}
