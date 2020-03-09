/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.util;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.ToDoubleFunction;
import java.util.Comparators;

/**
 * A comparison function, which imposes a <i>total ordering</i> on some
 * collection of objects.  Comparators can be passed to a sort method (such
 * as {@link Collections#sort(List,Comparator) Collections.sort} or {@link
 * Arrays#sort(Object[],Comparator) Arrays.sort}) to allow precise control
 * over the sort order.  Comparators can also be used to control the order of
 * certain data structures (such as {@link SortedSet sorted sets} or {@link
 * SortedMap sorted maps}), or to provide an ordering for collections of
 * objects that don't have a {@link Comparable natural ordering}.<p>
 * comparator是"比较"的抽象。Comparators可以被传递给排序方法（比如Collections#sort等）用来
 * 实现元素的比较过程，进而控制排序结果。Comparators也可以用来控制数据结构中元素的顺序，当然本质上
 * 依然是通过控制元素的比较过程，控制元素的排序结果，进而控制数据结构中元素的顺序
 *
 * The ordering imposed by a comparator <tt>c</tt> on a set of elements
 * <tt>S</tt> is said to be <i>consistent with equals</i> if and only if
 * <tt>c.compare(e1, e2)==0</tt> has the same boolean value as
 * <tt>e1.equals(e2)</tt> for every <tt>e1</tt> and <tt>e2</tt> in
 * <tt>S</tt>.<p>
 * 如果c.compare(e1, e2) == 0。那么e1.equals(e2)应该返回true
 *
 * Caution should be exercised when using a comparator capable of imposing an
 * ordering inconsistent with equals to order a sorted set (or sorted map).
 * Suppose a sorted set (or sorted map) with an explicit comparator <tt>c</tt>
 * is used with elements (or keys) drawn from a set <tt>S</tt>.  If the
 * ordering imposed by <tt>c</tt> on <tt>S</tt> is inconsistent with equals,
 * the sorted set (or sorted map) will behave "strangely."  In particular the
 * sorted set (or sorted map) will violate the general contract for set (or
 * map), which is defined in terms of <tt>equals</tt>.<p>
 * 实现一个Comparators的时候应该特别小心，需要考虑到比较对象的equals方法是否会与比较器的比较结果
 * 产生冲突。如果出现冲突情况，当这个比较器被用在有序集合中对集合元素进行比较时，就会出现许多奇怪
 * 的情况。下面拿TreeSet举了个例子
 *
 * For example, suppose one adds two elements {@code a} and {@code b} such that
 * {@code (a.equals(b) && c.compare(a, b) != 0)}
 * to an empty {@code TreeSet} with comparator {@code c}.
 * The second {@code add} operation will return
 * true (and the size of the tree set will increase) because {@code a} and
 * {@code b} are not equivalent from the tree set's perspective, even though
 * this is contrary to the specification of the
 * {@link Set#add Set.add} method.<p>
 *
 * Note: It is generally a good idea for comparators to also implement
 * <tt>java.io.Serializable</tt>, as they may be used as ordering methods in
 * serializable data structures (like {@link TreeSet}, {@link TreeMap}).  In
 * order for the data structure to serialize successfully, the comparator (if
 * provided) must implement <tt>Serializable</tt>.<p>
 * comparator最好也实现java.io.Serializable接口，因为在一些有序数据结构（TreeSet、TreeMap）中
 * 可能需要将数据结构进行序列化保存，此时除了序列化保存数据结构中的元素，还要序列化保存comparator
 * 否则在反序列化后，会丢失原来的排序规则信息。可参见TreeSet类的writeObject方法，里面会序列化comparator
 * 进行保存
 *
 * For the mathematically inclined, the <i>relation</i> that defines the
 * <i>imposed ordering</i> that a given comparator <tt>c</tt> imposes on a
 * given set of objects <tt>S</tt> is:<pre>
 *       {(x, y) such that c.compare(x, y) &lt;= 0}.
 * </pre> The <i>quotient</i> for this total order is:<pre>
 *       {(x, y) such that c.compare(x, y) == 0}.
 * </pre>
 *
 * It follows immediately from the contract for <tt>compare</tt> that the
 * quotient is an <i>equivalence relation</i> on <tt>S</tt>, and that the
 * imposed ordering is a <i>total order</i> on <tt>S</tt>.  When we say that
 * the ordering imposed by <tt>c</tt> on <tt>S</tt> is <i>consistent with
 * equals</i>, we mean that the quotient for the ordering is the equivalence
 * relation defined by the objects' {@link Object#equals(Object)
 * equals(Object)} method(s):<pre>
 *     {(x, y) such that x.equals(y)}. </pre>
 *
 * <p>Unlike {@code Comparable}, a comparator may optionally permit
 * comparison of null arguments, while maintaining the requirements for
 * an equivalence relation.
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <T> the type of objects that may be compared by this comparator
 * 函数式接口：因为Comparator仅仅代表了比较的过程，只需要输入元素，输出比价结果即可
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see Comparable
 * @see java.io.Serializable
 * @since 1.2
 */
@FunctionalInterface
public interface Comparator<T> {
    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * 比较两个参数的大小，返回负数、0、正数分别代表了第一个参数比较小，两者相等，第一个参数比较大
     *
     * In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.<p>
     * sgn(expression)操作会根据expression的取值是负值、0、正值，返回-1、0、+1.
     * sgn(compare(x, y)) == -sgn(compare(y, x))。这意味如果compare(x, y)抛出异常
     * 那么compare(y, x)也一定抛出异常
     *
     * The implementor must ensure that <tt>sgn(compare(x, y)) ==
     * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>compare(x, y)</tt> must throw an exception if and only
     * if <tt>compare(y, x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
     * <tt>compare(x, z)&gt;0</tt>.<p>
     * compare的实现还必须居然有传递性，compare(x, y) == 0，compare(y, z) == 0
     * 那么compare(x, z) == 0一定成立
     *
     * Finally, the implementor must ensure that <tt>compare(x, y)==0</tt>
     * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
     * <tt>z</tt>.<p>
     * 最后，如果compare(x, y) == 0，那么对于任意z，sgn(compare(x, z)) == sgn(compare(y, z))
     * 一定成立
     *
     * It is generally the case, but <i>not</i> strictly required that
     * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."
     * 通常来说，compare(x, y) == 0，那么x.equals(y)需要成立，这并非强制要求。但是如果一个
     * comparator违反了这一准则，最好需要特别标明这一特性。因为这是违法直觉的行为
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     * @throws NullPointerException if an argument is null and this
     *         comparator does not permit null arguments
     * @throws ClassCastException if the arguments' types prevent them from
     *         being compared by this comparator.
     */
    int compare(T o1, T o2);

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * comparator.  This method must obey the general contract of
     * {@link Object#equals(Object)}.  Additionally, this method can return
     * <tt>true</tt> <i>only</i> if the specified object is also a comparator
     * and it imposes the same ordering as this comparator.  Thus,
     * <code>comp1.equals(comp2)</code> implies that <tt>sgn(comp1.compare(o1,
     * o2))==sgn(comp2.compare(o1, o2))</tt> for every object reference
     * <tt>o1</tt> and <tt>o2</tt>.<p>
     *
     * Note that it is <i>always</i> safe <i>not</i> to override
     * <tt>Object.equals(Object)</tt>.  However, overriding this method may,
     * in some cases, improve performance by allowing programs to determine
     * that two distinct comparators impose the same order.
     *
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> only if the specified object is also
     *          a comparator and it imposes the same ordering as this
     *          comparator.
     * @see Object#equals(Object)
     * @see Object#hashCode()
     */
    boolean equals(Object obj);

    /**
     * Returns a comparator that imposes the reverse ordering of this
     * comparator.
     * 返回一个具有相反排序规则的comparator
     * java8新特性，接口方法使用default关键字来提供默认实现，子类无需实现default方法（因为是非抽象方法），
     * 而是会自动继承default方法。
     *
     * @return a comparator that imposes the reverse ordering of this
     *         comparator.
     * @since 1.8
     */
    default Comparator<T> reversed() {
        return Collections.reverseOrder(this);
    }

    /**
     * Returns a lexicographic-order comparator with another comparator.
     * If this {@code Comparator} considers two elements equal, i.e.
     * {@code compare(a, b) == 0}, {@code other} is used to determine the order.
     * 传入一个回调函数，返回一个新的comparetor，当c1，c2第一次比较相等时，调用回调函数
     * 进行第二次比较
     *
     * <p>The returned comparator is serializable if the specified comparator
     * is also serializable.
     *
     * @apiNote
     * For example, to sort a collection of {@code String} based on the length
     * and then case-insensitive natural ordering, the comparator can be
     * composed using following code,
     *
     * <pre>{@code
     *     Comparator<String> cmp = Comparator.comparingInt(String::length)
     *             .thenComparing(String.CASE_INSENSITIVE_ORDER);
     * }</pre>
     *
     * @param  other the other comparator to be used when this comparator
     *         compares two objects that are equal.
     * @return a lexicographic-order comparator composed of this and then the
     *         other comparator
     * @throws NullPointerException if the argument is null.
     * @since 1.8
     */
    default Comparator<T> thenComparing(Comparator<? super T> other) {
        Objects.requireNonNull(other);
        return (Comparator<T> & Serializable) (c1, c2) -> {
            int res = compare(c1, c2);
            return (res != 0) ? res : other.compare(c1, c2);
        };
    }

    /**
     * Returns a lexicographic-order comparator with a function that
     * extracts a key to be compared with the given {@code Comparator}.
     * 先比较c1,c1，在c1,c2相等的情况下运用 comparing(keyExtractor, keyComparator)进行比较
     *
     * @implSpec This default implementation behaves as if {@code
     *           thenComparing(comparing(keyExtractor, cmp))}.
     *
     * @param  <U>  the type of the sort key
     * @param  keyExtractor the function used to extract the sort key
     * @param  keyComparator the {@code Comparator} used to compare the sort key
     * @return a lexicographic-order comparator composed of this comparator
     *         and then comparing on the key extracted by the keyExtractor function
     * @throws NullPointerException if either argument is null.
     * @see #comparing(Function, Comparator)
     * @see #thenComparing(Comparator)
     * @since 1.8
     */
    default <U> Comparator<T> thenComparing(
            Function<? super T, ? extends U> keyExtractor,
            Comparator<? super U> keyComparator)
    {
        return thenComparing(comparing(keyExtractor, keyComparator));
    }

    /**
     * Returns a lexicographic-order comparator with a function that
     * extracts a {@code Comparable} sort key.
     *
     * @implSpec This default implementation behaves as if {@code
     *           thenComparing(comparing(keyExtractor))}.
     *
     * @param  <U>  the type of the {@link Comparable} sort key
     * @param  keyExtractor the function used to extract the {@link
     *         Comparable} sort key
     * @return a lexicographic-order comparator composed of this and then the
     *         {@link Comparable} sort key.
     * @throws NullPointerException if the argument is null.
     * @see #comparing(Function)
     * @see #thenComparing(Comparator)
     * @since 1.8
     */
    default <U extends Comparable<? super U>> Comparator<T> thenComparing(
            Function<? super T, ? extends U> keyExtractor)
    {
        return thenComparing(comparing(keyExtractor));
    }

    /**
     * Returns a lexicographic-order comparator with a function that
     * extracts a {@code int} sort key.
     *
     * @implSpec This default implementation behaves as if {@code
     *           thenComparing(comparingInt(keyExtractor))}.
     *
     * @param  keyExtractor the function used to extract the integer sort key
     * @return a lexicographic-order comparator composed of this and then the
     *         {@code int} sort key
     * @throws NullPointerException if the argument is null.
     * @see #comparingInt(ToIntFunction)
     * @see #thenComparing(Comparator)
     * @since 1.8
     */
    default Comparator<T> thenComparingInt(ToIntFunction<? super T> keyExtractor) {
        return thenComparing(comparingInt(keyExtractor));
    }

    /**
     * Returns a lexicographic-order comparator with a function that
     * extracts a {@code long} sort key.
     *
     * @implSpec This default implementation behaves as if {@code
     *           thenComparing(comparingLong(keyExtractor))}.
     *
     * @param  keyExtractor the function used to extract the long sort key
     * @return a lexicographic-order comparator composed of this and then the
     *         {@code long} sort key
     * @throws NullPointerException if the argument is null.
     * @see #comparingLong(ToLongFunction)
     * @see #thenComparing(Comparator)
     * @since 1.8
     */
    default Comparator<T> thenComparingLong(ToLongFunction<? super T> keyExtractor) {
        return thenComparing(comparingLong(keyExtractor));
    }

    /**
     * Returns a lexicographic-order comparator with a function that
     * extracts a {@code double} sort key.
     *
     * @implSpec This default implementation behaves as if {@code
     *           thenComparing(comparingDouble(keyExtractor))}.
     *
     * @param  keyExtractor the function used to extract the double sort key
     * @return a lexicographic-order comparator composed of this and then the
     *         {@code double} sort key
     * @throws NullPointerException if the argument is null.
     * @see #comparingDouble(ToDoubleFunction)
     * @see #thenComparing(Comparator)
     * @since 1.8
     */
    default Comparator<T> thenComparingDouble(ToDoubleFunction<? super T> keyExtractor) {
        return thenComparing(comparingDouble(keyExtractor));
    }

    /**
     * Returns a comparator that imposes the reverse of the <em>natural
     * ordering</em>.
     * 返回一个与自然序相反的comparator
     * 自然序就是升序（从小到大），与自然序相反是降序（从大到小）
     * <p>The returned comparator is serializable and throws {@link
     * NullPointerException} when comparing {@code null}.
     * java8新特性：接口静态方法
     *
     * @param  <T> the {@link Comparable} type of element to be compared
     * @return a comparator that imposes the reverse of the <i>natural
     *         ordering</i> on {@code Comparable} objects.
     * @see Comparable
     * @since 1.8
     */
    public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
        return Collections.reverseOrder();
    }

    /**
     * Returns a comparator that compares {@link Comparable} objects in natural
     * order.
     * 返回一个自然序的comparator，自然序就是升序（从小到大）
     * <p>The returned comparator is serializable and throws {@link
     * NullPointerException} when comparing {@code null}.
     *
     * @param  <T> the {@link Comparable} type of element to be compared
     * @return a comparator that imposes the <i>natural ordering</i> on {@code
     *         Comparable} objects.
     * @see Comparable
     * @since 1.8
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
        return (Comparator<T>) Comparators.NaturalOrderComparator.INSTANCE;
    }

    /**
     * Returns a null-friendly comparator that considers {@code null} to be
     * less than non-null. When both are {@code null}, they are considered
     * equal. If both are non-null, the specified {@code Comparator} is used
     * to determine the order. If the specified comparator is {@code null},
     * then the returned comparator considers all non-null values to be equal.
     * 创建一个"null友好"的comparator，此comparator认为null比所有非null的值都要小，
     * 并且两个null是相等的，对应两个非null的值则交给参数指定的comparator进行比较。
     * 如果参数comparator是一个null，那么所有非null的值都被认为是相等的
     * nullFirst的意思是null排在最前面（null最小）
     *
     * <p>The returned comparator is serializable if the specified comparator
     * is serializable.
     *
     * @param  <T> the type of the elements to be compared
     * @param  comparator a {@code Comparator} for comparing non-null values
     * @return a comparator that considers {@code null} to be less than
     *         non-null, and compares non-null objects with the supplied
     *         {@code Comparator}.
     * @since 1.8
     */
    public static <T> Comparator<T> nullsFirst(Comparator<? super T> comparator) {
        return new Comparators.NullComparator<>(true, comparator);
    }

    /**
     * Returns a null-friendly comparator that considers {@code null} to be
     * greater than non-null. When both are {@code null}, they are considered
     * equal. If both are non-null, the specified {@code Comparator} is used
     * to determine the order. If the specified comparator is {@code null},
     * then the returned comparator considers all non-null values to be equal.
     * 与nullsFirst相反，这里返回的comparator认为null比所有的非null都要大，两个null值相等
     * 两个非null值交给参数comparator进行比较。如果参数为null，则认为所有非null值相等
     *
     * <p>The returned comparator is serializable if the specified comparator
     * is serializable.
     *
     * @param  <T> the type of the elements to be compared
     * @param  comparator a {@code Comparator} for comparing non-null values
     * @return a comparator that considers {@code null} to be greater than
     *         non-null, and compares non-null objects with the supplied
     *         {@code Comparator}.
     * @since 1.8
     */
    public static <T> Comparator<T> nullsLast(Comparator<? super T> comparator) {
        return new Comparators.NullComparator<>(false, comparator);
    }

    /**
     * Accepts a function that extracts a sort key from a type {@code T}, and
     * returns a {@code Comparator<T>} that compares by that sort key using
     * the specified {@link Comparator}.
     * 接受一个从对象中提取key（key是比较的对象的关键值）的函数，以及接受一个比较key的函数，
     * 返回一个用来排序对象的函数
     *
     * <p>The returned comparator is serializable if the specified function
     * and comparator are both serializable.
     *
     * @apiNote
     * For example, to obtain a {@code Comparator} that compares {@code
     * Person} objects by their last name ignoring case differences,
     *
     * <pre>{@code
     *     Comparator<Person> cmp = Comparator.comparing(
     *             Person::getLastName,
     *             String.CASE_INSENSITIVE_ORDER);
     * }</pre>
     *
     *
     * @param  <T> the type of element to be compared
     * @param  <U> the type of the sort key
     * @param  keyExtractor the function used to extract the sort key
     * @param  keyComparator the {@code Comparator} used to compare the sort key
     * @return a comparator that compares by an extracted key using the
     *         specified {@code Comparator}
     * @throws NullPointerException if either argument is null
     * @since 1.8
     */
    public static <T, U> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor,
            Comparator<? super U> keyComparator)
    {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(keyComparator);
        return (Comparator<T> & Serializable)
            (c1, c2) -> keyComparator.compare(keyExtractor.apply(c1), // 提取key进行比较
                                              keyExtractor.apply(c2));
    }

    /**
     * Accepts a function that extracts a {@link java.lang.Comparable
     * Comparable} sort key from a type {@code T}, and returns a {@code
     * Comparator<T>} that compares by that sort key.
     *
     * <p>The returned comparator is serializable if the specified function
     * is also serializable.
     *
     * @apiNote
     * For example, to obtain a {@code Comparator} that compares {@code
     * Person} objects by their last name,
     *
     * <pre>{@code
     *     Comparator<Person> byLastName = Comparator.comparing(Person::getLastName);
     * }</pre>
     *
     * @param  <T> the type of element to be compared
     * @param  <U> the type of the {@code Comparable} sort key
     * @param  keyExtractor the function used to extract the {@link
     *         Comparable} sort key
     * @return a comparator that compares by an extracted key
     * @throws NullPointerException if the argument is null
     * @since 1.8
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor)
    {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
                // 提取实例的key，然后使用key的compareTo方法进行比较，这要求key实现了comparable接口
            (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
    }

    /**
     * Accepts a function that extracts an {@code int} sort key from a type
     * {@code T}, and returns a {@code Comparator<T>} that compares by that
     * sort key.
     *
     * <p>The returned comparator is serializable if the specified function
     * is also serializable.
     *
     * @param  <T> the type of element to be compared
     * @param  keyExtractor the function used to extract the integer sort key
     * @return a comparator that compares by an extracted key
     * @see #comparing(Function)
     * @throws NullPointerException if the argument is null
     * @since 1.8
     */
    public static <T> Comparator<T> comparingInt(ToIntFunction<? super T> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
            (c1, c2) -> Integer.compare(keyExtractor.applyAsInt(c1), keyExtractor.applyAsInt(c2));
    }

    /**
     * Accepts a function that extracts a {@code long} sort key from a type
     * {@code T}, and returns a {@code Comparator<T>} that compares by that
     * sort key.
     *
     * <p>The returned comparator is serializable if the specified function is
     * also serializable.
     *
     * @param  <T> the type of element to be compared
     * @param  keyExtractor the function used to extract the long sort key
     * @return a comparator that compares by an extracted key
     * @see #comparing(Function)
     * @throws NullPointerException if the argument is null
     * @since 1.8
     */
    public static <T> Comparator<T> comparingLong(ToLongFunction<? super T> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
            (c1, c2) -> Long.compare(keyExtractor.applyAsLong(c1), keyExtractor.applyAsLong(c2));
    }

    /**
     * Accepts a function that extracts a {@code double} sort key from a type
     * {@code T}, and returns a {@code Comparator<T>} that compares by that
     * sort key.
     *
     * <p>The returned comparator is serializable if the specified function
     * is also serializable.
     *
     * @param  <T> the type of element to be compared
     * @param  keyExtractor the function used to extract the double sort key
     * @return a comparator that compares by an extracted key
     * @see #comparing(Function)
     * @throws NullPointerException if the argument is null
     * @since 1.8
     */
    public static<T> Comparator<T> comparingDouble(ToDoubleFunction<? super T> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
            (c1, c2) -> Double.compare(keyExtractor.applyAsDouble(c1), keyExtractor.applyAsDouble(c2));
    }
}
