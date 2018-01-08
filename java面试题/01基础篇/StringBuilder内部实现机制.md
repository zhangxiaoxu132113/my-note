StringBuilder内部有一个字符数组，代码如下

char[] value;   //字符数组
int count;      //字符串长度
每一次append操作都是将新的字符串加入到可变长的字符数组中，长度计算方式与ArrayList类似。调用toString()方法时，new一个String对象即可。

public String toString() {
        return new String(value, 0, count);// Create a copy, don't share the array
}
ps: StringBuffer是线程安全的，StringBuilder是非线程安全的。