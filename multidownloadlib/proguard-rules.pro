-printusage unused.txt
-optimizationpasses 5
-printmapping mapping.txt
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature

-keep public class com.zcoup.multidownload.MultiDownloadManager{
    *;
}
-keep public class com.zcoup.multidownload.entitis.FileInfo{
    *;
}

-keep public interface com.zcoup.multidownload.service.LoadListener{
    *;
}
