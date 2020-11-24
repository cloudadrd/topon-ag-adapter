# topon接入步骤

1. 导入libs下的aar包,主项目下新建libs文件夹,主项目下的build.gradle文件下加入以下代码：

   ```groovy
   dependencies {
   	api fileTree(include: ['*.jar','*.aar'], dir: 'libs')
   }
   ```

![image-20201124105440588](./md-images/image-20201124105314610.png)

2. 在主项目目录下新建network目录，复制以下四个包，加入，穿山甲pangle 优量汇gdt 快手kuaishou 还有adsgreat

   ![image-20201124105923260](./md-images/image-20201124105923260.png)

3. 复制内容到主项目的progurad配置文件里，一样的覆盖不一样的添加

   ![image-20201124110719468](./md-images/image-20201124110719468.png)

4. 添加主项目的build.gradle配置

   ![image-20201124110906431](./md-images/image-20201124110906431.png)



   * 添加以下内容（参考哦红线标记处）

     ![image-20201124110941717](./md-images/image-20201124110941717.png)

   * 继续添加（参考哦红线标记处）

     ![image-20201124111236956](./md-images/image-20201124111236956.png)

   * 继续添加（参考哦红线标记处），往下未显示完的内容全部添加

     ![image-20201124111419748](./md-images/image-20201124111419748.png)

5. 新建包名com.anythink.custom.adapter，在此包下复制以AdsGreat为前缀的类文件至新建的包下

   ![image-20201124111631867](./md-images/image-20201124111631867.png)

6. 然后在主项目的proguard-rules.pro下加入以下内容

   ```properties
   -keep class com.anythink.custom.** {*;}
   ```

* 至于各种类型的广告接入，布局等实现，参考demo下的代码

  ![image-20201124113402152](./md-images/image-20201124113402152.png)