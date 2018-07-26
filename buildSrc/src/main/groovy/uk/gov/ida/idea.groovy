package uk.gov.ida

import org.gradle.api.XmlProvider

class RunConfig {

    final XmlProvider provider;

    RunConfig(XmlProvider provider) {
        this.provider = provider
    }

    void configuration(attributes, closure) {
        def runManagerNode = provider.node.component.find { it.@name == 'RunManager' }

        if (runManagerNode.find { it.@name == attributes['name'] } != null) {
            return
        }

        def builder = new NodeBuilder()
        builder.setCurrent(runManagerNode)
        builder.configuration(attributes, closure)
    }

    def ensureApplicationConfigurationExists(
            String name,
            String packagePattern,
            String mainClassName,
            String moduleName,
            String programParameters) {
        configuration(name: name, type: 'Application', factoryName: 'Application') {
            extension(name: 'coverage', enabled: 'false', merge: 'false', runner: 'idea') {
                pattern {
                    option(name: 'PATTERN', value: packagePattern)
                    option(name: 'ENABLED', value: 'true')
                }
            }
            option(name: 'MAIN_CLASS_NAME', value: mainClassName)
            option(name: 'VM_PARAMETERS', value: '')
            option(name: 'PROGRAM_PARAMETERS', value: programParameters)
            option(name: 'WORKING_DIRECTORY', value: '$PROJECT_DIR$')
            option(name: 'ALTERNATIVE_JRE_PATH_ENABLED', value: 'false')
            option(name: 'ALTERNATIVE_JRE_PATH', value: '')
            option(name: 'ENABLE_SWING_INSPECTOR', value: 'false')
            option(name: 'ENV_VARIABLES')
            option(name: 'PASS_PARENT_ENVS', value: 'true')
            module(name: moduleName)
            RunnerSettings(RunnerId: 'Debug') {
                option(name: 'DEBUG_PORT', value: '')
                option(name: 'TRANSPORT', value: '0')
                option(name: 'LOCAL', value: 'true')
            }
        }
    }

    void ensureDebugConfigurationExists(
            String name,
            String port) {
        configuration(name: name, type: 'Remote', factoryName: 'Remote') {
            option(name: 'USE_SOCKET_TRANSPORT', value: 'true')
            option(name: 'SERVER_MODE', value: 'false')
            option(name: 'SHMEM_ADDRESS', value: 'javadebug')
            option(name: 'HOST', value: 'localhost')
            option(name: 'PORT', value: port)
            RunnerSettings(RunnerId: 'Debug') {
                option(name: 'DEBUG_PORT', value: port)
                option(name: 'TRANSPORT', value: '0')
                option(name: 'LOCAL', value: 'true')
            }
            ConfigurationWrapper(RunnerId: 'Debug')
        }
    }

    void ensureJUnitConfigurationExists(String name, String moduleName) {
        configuration(name: name, type: 'JUnit', factoryName: 'JUnit') {
            module(name: moduleName)
            option(name: 'ALTERNATIVE_JRE_PATH_ENABLED', value: 'false')
            option(name: 'ALTERNATIVE_JRE_PATH', value: '')
            option(name: 'PACKAGE_NAME', value: '')
            option(name: 'MAIN_CLASS_NAME', value: '')
            option(name: 'METHOD_NAME', value: '')
            option(name: 'TEST_OBJECT', value: 'package')
            option(name: 'VM_PARAMETERS', value: '-ea')
            option(name: 'PARAMETERS', value: '')
            option(name: 'WORKING_DIRECTORY', value: 'file://$PROJECT_DIR$')
            option(name: 'PASS_PARENT_ENVS', value: 'true')
            option(name: 'ENV_VARIABLES')
            option(name: 'TEST_SEARCH_SCOPE') {
                value(defaultName: 'moduleWithDependencies')
            }
            RunnerSettings(RunnerId: 'Run')
            ConfigurationWrapper(RunnerId: 'Run')
        }
    }
}

