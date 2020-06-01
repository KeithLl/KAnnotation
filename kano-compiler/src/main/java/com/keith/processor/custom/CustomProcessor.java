package com.keith.processor.custom;
import com.keith.annotations.router.ClassLink;
import com.keith.annotations.router.FieldParams;
import com.keith.annotations.router.MethodLink;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by KeithLee on 2020/5/30.
 * Introduction:
 */
public class CustomProcessor extends AbstractProcessor {

    private Filer filer;

    public CustomProcessor() {
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(ClassLink.class.getCanonicalName());
        set.add(FieldParams.class.getCanonicalName());
        set.add(MethodLink.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler(); // for creating file
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) {
            return false;
        }
        Set<? extends Element> elementsAnnotatedWith =
                roundEnvironment.getElementsAnnotatedWith(ClassLink.class);

        // generate HashMap
        FieldSpec mRouterMap =
                FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class),
                        ClassName.get(String.class), ClassName.get(String.class)),
                        "mRouterMap",
                        Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", HashMap.class)
                        .build();

        //init
        CodeBlock.Builder builder = CodeBlock.builder();

        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            //获取类名
            String className = typeElement.getQualifiedName().toString();
            //获取注解对应的key
            ClassLink annotation = typeElement.getAnnotation(ClassLink.class);
            String value = annotation.value();

            builder.addStatement("mRouterMap.put($S,$S)", value, className);
        }

        CodeBlock codeBlock = builder.build();

        //RouterClass
        TypeSpec routeClass = TypeSpec.classBuilder("RouterConstants")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(mRouterMap)
                .addStaticBlock(codeBlock)
                .build();
        try {
            // build com.example.HelloWorld.java
            JavaFile javaFile = JavaFile.builder("com.jxedt.router", routeClass)
                    .addFileComment(" This codes are generated automatically. Do not modify!")
                    .build();
            // write to file
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
