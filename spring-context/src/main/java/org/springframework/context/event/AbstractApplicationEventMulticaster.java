/*
 * Copyright 2002-2018 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * {@link ApplicationEventMulticaster}�ӿڵĳ���ʵ�֣�*�ṩ�����ļ�����ע�����ʵ�֡�
 *
 * Ĭ������²�����ͬһ���������Ķ��ʵ����*��Ϊ�������������������ӵ�Set�С�, ���ڱ���
 * ApplicationListener����ļ��������ͨ����collectionClass��* bean���Ը��ǡ�
 *
 * ʵ��ApplicationEventMulticasterͨ��{@link #multicastEvent}����������������������Ը��Ǵ˷���ʵ���Զ���ApplicationEventMulticaster
 *
 * {@link SimpleApplicationEventMulticaster}��spring�ṩ��һ��Ĭ��ʵ�֣�
 * ���򵥵ؽ������¼��㲥��������ע��������������ڹ㲥�Ĺ����п���ͨ���߳�ʵ���첽֪ͨ ��
 * ����Щ���棬���ʵʩ���ܸ����ӡ�
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 1.2.3
 * @see #getApplicationListeners(ApplicationEvent, ResolvableType)
 * @see SimpleApplicationEventMulticaster
 */
public abstract class AbstractApplicationEventMulticaster
		implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {

	private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);

	final Map<ListenerCacheKey, ListenerRetriever> retrieverCache =
			new ConcurrentHashMap<ListenerCacheKey, ListenerRetriever>(64);

	private ClassLoader beanClassLoader;

	private BeanFactory beanFactory;

	private Object retrievalMutex = this.defaultRetriever;


	/**
	 * ʵ��BeanClassLoaderAware����ȡ����beanClass���������
	 */
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	/**
	 * ʵ��BeanFactoryAware����ȡ�ڲ�����bean��������BeanFactory
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		if (beanFactory instanceof ConfigurableBeanFactory) {
			ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
			if (this.beanClassLoader == null) {
				this.beanClassLoader = cbf.getBeanClassLoader();
			}
			this.retrievalMutex = cbf.getSingletonMutex();
		}
	}

	/**
	 * ��ȡ�ڲ�����bean��������BeanFactory
	 * @return
	 */
	private BeanFactory getBeanFactory() {
		if (this.beanFactory == null) {
			throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans " +
					"because it is not associated with a BeanFactory");
		}
		return this.beanFactory;
	}


	/**
	 * ���һ������������
	 * @param  listener ApplicationListener��ʵ����
	 */
	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {
		synchronized (this.retrievalMutex) {
			// Explicitly remove target for a proxy, if registered already,
			// in order to avoid double invocations of the same listener.
			Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
			if (singletonTarget instanceof ApplicationListener) {
				this.defaultRetriever.applicationListeners.remove(singletonTarget);
			}
			this.defaultRetriever.applicationListeners.add(listener);
			this.retrieverCache.clear();
		}
	}

	/**
	 * ���һ������������
	 * @param listenerBeanName ApplicationListener��ʵ�ֶ�Ӧ��bean����
	 */
	@Override
	public void addApplicationListenerBean(String listenerBeanName) {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
			this.retrieverCache.clear();
		}
	}

	/**
	 * ɾ��һ������������
	 * @param  listener ApplicationListener��ʵ����
	 */
	@Override
	public void removeApplicationListener(ApplicationListener<?> listener) {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListeners.remove(listener);
			this.retrieverCache.clear();
		}
	}


	/**
	 * ɾ��һ������������
	 * @param listenerBeanName ApplicationListener��ʵ�ֶ�Ӧ��bean����
	 */
	@Override
	public void removeApplicationListenerBean(String listenerBeanName) {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
			this.retrieverCache.clear();
		}
	}

	/**
	 * ɾ�����м���������
	 */
	@Override
	public void removeAllListeners() {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListeners.clear();
			this.defaultRetriever.applicationListenerBeans.clear();
			this.retrieverCache.clear();
		}
	}


	/**
	 * ���ذ������м�����ApplicationListeners�ļ��ϡ�
	 * @see org.springframework.context.ApplicationListener
	 */
	protected Collection<ApplicationListener<?>> getApplicationListeners() {
		synchronized (this.retrievalMutex) {
			return this.defaultRetriever.getApplicationListeners();
		}
	}

	/**
	 * ����������¼����ͺ�Դ���Ͷ�Ӧ��ApplicationListeners���ϡ� ��ƥ������ڻᾡ�类�ų����⡣
	 * @param event ApplicationEvent�����¼�����
	 * @param eventType  ApplicationEvent�����¼���������
	 * @return a Collection of ApplicationListeners����
	 * @see org.springframework.context.ApplicationListener
	 */
	protected Collection<ApplicationListener<?>> getApplicationListeners(
			ApplicationEvent event, ResolvableType eventType) {

		//���ݲ�����ȡ�������ListenerCacheKey  cacheKey
		Object source = event.getSource();
		Class<?> sourceType = (source != null ? source.getClass() : null);
		ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);

		//��retrieverCache�����л�ȡ���������󼯺�
		ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
		if (retriever != null) {
			return retriever.getApplicationListeners();
		}

		if (this.beanClassLoader == null ||
				(ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) &&
						(sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader)))) {
			// ��ȡ���ϲ����õ�����retrieverCache
			// 2�μ��
			synchronized (this.retrievalMutex) {
				retriever = this.retrieverCache.get(cacheKey);
				if (retriever != null) {
					return retriever.getApplicationListeners();
				}
				retriever = new ListenerRetriever(true);
				//�����¼����ͺ�Դ���ͻ�ȡ����������
				Collection<ApplicationListener<?>> listeners =
						retrieveApplicationListeners(eventType, sourceType, retriever);
				this.retrieverCache.put(cacheKey, retriever);
				return listeners;
			}
		}
		else {
			// No ListenerRetriever caching -> no synchronization necessary
			return retrieveApplicationListeners(eventType, sourceType, null);
		}
	}

	/**
	 * �����¼����ͺ�Դ���ͻ�ȡ����������
	 * @param eventType the event type
	 * @param sourceType the event source type
	 * @param retriever the ListenerRetriever, if supposed to populate one (for caching purposes)
	 * @return the pre-filtered list of application listeners for the given event and source type
	 */
	private Collection<ApplicationListener<?>> retrieveApplicationListeners(
			ResolvableType eventType, Class<?> sourceType, ListenerRetriever retriever) {

		List<ApplicationListener<?>> allListeners = new ArrayList<ApplicationListener<?>>();
		Set<ApplicationListener<?>> listeners;
		Set<String> listenerBeans;
		synchronized (this.retrievalMutex) {
			listeners = new LinkedHashSet<ApplicationListener<?>>(this.defaultRetriever.applicationListeners);
			listenerBeans = new LinkedHashSet<String>(this.defaultRetriever.applicationListenerBeans);
		}

		/** ��������ApplicationListener �Ƿ�ƥ��eventType��sourceType **/
		for (ApplicationListener<?> listener : listeners) {
			if (supportsEvent(listener, eventType, sourceType)) {
				if (retriever != null) {
					retriever.applicationListeners.add(listener);
				}
				allListeners.add(listener);
			}
		}
		/** ��������applicationListenerBeans �Ƿ�ƥ��eventType��sourceType **/
		if (!listenerBeans.isEmpty()) {
			BeanFactory beanFactory = getBeanFactory();
			for (String listenerBeanName : listenerBeans) {
				try {
					Class<?> listenerType = beanFactory.getType(listenerBeanName);
					if (listenerType == null || supportsEvent(listenerType, eventType)) {
						ApplicationListener<?> listener =
								beanFactory.getBean(listenerBeanName, ApplicationListener.class);
						if (!allListeners.contains(listener) && supportsEvent(listener, eventType, sourceType)) {
							if (retriever != null) {
								retriever.applicationListenerBeans.add(listenerBeanName);
							}
							allListeners.add(listener);
						}
					}
				}
				catch (NoSuchBeanDefinitionException ex) {
					// Singleton listener instance (without backing bean definition) disappeared -
					// probably in the middle of the destruction phase
				}
			}
		}
		AnnotationAwareOrderComparator.sort(allListeners);
		return allListeners;
	}

	/**
	 * �ڳ���ʵ����֮ǰ��ͨ�������ͨ���������¼�*��������ǰ������������ , * <p>����˷�����Ϊ��һ�δ��ݷ��ظ�����������{@code true}����ͨ�����·�ʽ��������ȫ����������ʵ��
	 * {@link #supportsEvent(ApplicationListener,ResolvableType, Class)}  call afterwards.
	 * @param listenerType the listener's type as determined by the BeanFactory
	 * @param eventType the event type to check
	 * @return whether the given listener should be included in the candidates
	 * for the given event type
	 */
	protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
		if (GenericApplicationListener.class.isAssignableFrom(listenerType) ||
				SmartApplicationListener.class.isAssignableFrom(listenerType)) {
			return true;
		}
		ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
		return (declaredEventType == null || declaredEventType.isAssignableFrom(eventType));
	}

	/**
	 * ȷ�������������Ƿ�֧�ָ����¼��� ,Ĭ��ʵ�ּ��{@link SmartApplicationListener} ��{@link GenericApplicationListener}�ӿڡ�,
	 * ���ڱ�׼* {@link ApplicationListener}����ʹ��{@link GenericApplicationListenerAdapter}����ʡһ��������Ŀ�����������͡�
	 * @param listener the target listener to check
	 * @param eventType the event type to check against
	 * @param sourceType the source type to check against
	 * @return whether the given listener should be included in the candidates
	 * for the given event type
	 */
	protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, Class<?> sourceType) {
		GenericApplicationListener smartListener = (listener instanceof GenericApplicationListener ?
				(GenericApplicationListener) listener : new GenericApplicationListenerAdapter(listener));
		return (smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType));
	}


	/**
	 * ListenerRetrievers�Ļ�����������¼����ͺ�Դ���͡�
	 */
	private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {

		private final ResolvableType eventType;

		private final Class<?> sourceType;

		public ListenerCacheKey(ResolvableType eventType, Class<?> sourceType) {
			this.eventType = eventType;
			this.sourceType = sourceType;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			ListenerCacheKey otherKey = (ListenerCacheKey) other;
			return (ObjectUtils.nullSafeEquals(this.eventType, otherKey.eventType) &&
					ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType));
		}

		@Override
		public int hashCode() {
			return (ObjectUtils.nullSafeHashCode(this.eventType) * 29 + ObjectUtils.nullSafeHashCode(this.sourceType));
		}

		@Override
		public String toString() {
			return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType.getName() + "]";
		}

		@Override
		public int compareTo(ListenerCacheKey other) {
			int result = 0;
			if (this.eventType != null) {
				result = this.eventType.toString().compareTo(other.eventType.toString());
			}
			if (result == 0 && this.sourceType != null) {
				result = this.sourceType.getName().compareTo(other.sourceType.getName());
			}
			return result;
		}
	}


	/**
	 *  Helper�࣬����װ��һ���ض���Ŀ����������*������Ч����Ԥ���˵��������� , * <p>ÿ���¼����ͺ�Դ���Ͷ��Ỻ��˰��������ʵ����
	 */
	private class ListenerRetriever {

		/**
		 * {@link #addApplicationListener}����ע��ļ���������
		 */
		public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<ApplicationListener<?>>();

		/**
		 * {@link #addApplicationListenerBean}����ע��ļ���������
		 */
		public final Set<String> applicationListenerBeans = new LinkedHashSet<String>();

		/**
		 * �Ƿ񲻹��˵�applicationListenerBeans��ͬ�ļ���������
		 */
		private final boolean preFiltered;

		public ListenerRetriever(boolean preFiltered) {
			this.preFiltered = preFiltered;
		}

		/**
		 * @return �������еļ���������
		 */
		public Collection<ApplicationListener<?>> getApplicationListeners() {

			List<ApplicationListener<?>> allListeners = new ArrayList<ApplicationListener<?>>(
					this.applicationListeners.size() + this.applicationListenerBeans.size());

			allListeners.addAll(this.applicationListeners);
			if (!this.applicationListenerBeans.isEmpty()) {
				BeanFactory beanFactory = getBeanFactory();
				for (String listenerBeanName : this.applicationListenerBeans) {
					try {
						ApplicationListener<?> listener = beanFactory.getBean(listenerBeanName, ApplicationListener.class);
						if (this.preFiltered || !allListeners.contains(listener)) {
							allListeners.add(listener);
						}
					}
					catch (NoSuchBeanDefinitionException ex) {
						// Singleton listener instance (without backing bean definition) disappeared -
						// probably in the middle of the destruction phase
					}
				}
			}
			AnnotationAwareOrderComparator.sort(allListeners);
			return allListeners;
		}
	}

}
