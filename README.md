# XposedBase
开发Xposed模块工具库

- 每次hook，尽量在try catch块中，因为某处异常会导致后面代码无法执行
- 不要在实现了IXposedHookLoadPackage接口的类中修改成员变量，没有作用，不同的包名xposed会生成不同的hook子类。
