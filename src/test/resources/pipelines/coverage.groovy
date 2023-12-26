pipeline {
    agent any
    stages {
        stage('Generate Jacoco XML') {
            steps {
                script {
                    def jacocoXmlContent = '''<?xml version="1.0" encoding="UTF-8"?>
<report name="Example">
    <sessioninfo id="session1" start="2023-01-01T12:00:00" dump="2023-01-01T12:30:00" />
    <group name="Group1">
        <package name="com.example.package">
            <class name="com.example.package.ExampleClass" sourcefilename="ExampleClass.java">
                <method name="exampleMethod" desc="()V" line="10">
                    <counter type="INSTRUCTION" missed="50" covered="50" />
                    <counter type="BRANCH" missed="0" covered="50" />
                    <counter type="LINE" missed="5" covered="5" />
                    <counter type="COMPLEXITY" missed="5" covered="5" />
                    <counter type="METHOD" missed="1" covered="1" />
                    <counter type="CLASS" missed="0" covered="1" />
                </method>
            </class>
        </package>
    </group>
</report>
                    '''
                    writeFile(file: 'jacoco.xml', text: jacocoXmlContent, encoding: 'UTF-8')
                }
            }
        }
        stage('Record Test and coverage') {
            steps {
                recordCoverage(tools: [[parser: 'JACOCO']])
            }
        }
    }
}
