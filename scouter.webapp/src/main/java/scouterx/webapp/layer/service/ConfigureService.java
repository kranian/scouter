package scouterx.webapp.layer.service;

import scouterx.webapp.framework.client.server.Server;
import scouterx.webapp.layer.consumer.ConfigureConsumer;
import scouterx.webapp.layer.consumer.ConfigureKVHandleConsumer;
import scouterx.webapp.model.configure.ConfApplyScopeEnum;
import scouterx.webapp.model.configure.ConfObjectState;
import scouterx.webapp.model.configure.ConfigureData;
import scouterx.webapp.request.SetConfigKvRequest;
import scouterx.webapp.request.SetConfigRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author yosong.heo (yosong.heo@gmail.com) on 2023. 2. 17.
 */
public class ConfigureService {
    private final ConfigureConsumer configureConsumer;
    private final ConfigureKVHandleConsumer configureKVHandleConsumer;

    public ConfigureService(){
        this.configureConsumer = new ConfigureConsumer();
        this.configureKVHandleConsumer = new ConfigureKVHandleConsumer();
    }
    public ConfigureData retrieveServerConfig(final Server server) {
        return this.configureConsumer.retrieveServerConfig(server,true);
    }

    public ConfigureData retrieveObjectConfig(int objHash, Server server) {
        return this.configureConsumer.retrieveObjectConfig(objHash,server);
    }

    public ConfigureData saveServerConfig(SetConfigRequest configRequest, Server server) {
        return this.configureConsumer.saveServerConfig(configRequest,server);
    }

    public ConfigureData saveObjectConfig(SetConfigRequest configRequest, int objHash, Server server) {
        return this.configureConsumer.saveObjectConfig(configRequest,objHash,server);
    }

    public List<ConfObjectState> saveObjTypConfig(String objType, Server server, SetConfigKvRequest configRequest) {
        Optional<List<ConfObjectState>> optional = this.configureKVHandleConsumer.applyConfig(ConfApplyScopeEnum.TYPE_IN_SERVER,configRequest,objType,server);
        return optional.isPresent() ? optional.get() : Collections.emptyList();
    }
    public List<ConfObjectState> saveObjFamilyConfig(String objFamily, Server server, SetConfigKvRequest configRequest) {
        Optional<List<ConfObjectState>> optional = this.configureKVHandleConsumer.applyConfig(ConfApplyScopeEnum.FAMILY_IN_SERVER,configRequest,objFamily,server);
        return optional.isPresent() ? optional.get() : Collections.emptyList();
    }
    public List<ConfObjectState> saveObjTypAllConfig(String objType, Server server, SetConfigKvRequest configRequest) {
        Optional<List<ConfObjectState>> optional = this.configureKVHandleConsumer.applyConfig(ConfApplyScopeEnum.TYPE_ALL,configRequest,objType,server);
        return optional.isPresent() ? optional.get() : Collections.emptyList();
    }
    public List<ConfObjectState> saveObjFamilyAllConfig(String objFamily, Server server, SetConfigKvRequest configRequest) {
        Optional<List<ConfObjectState>> optional = this.configureKVHandleConsumer.applyConfig(ConfApplyScopeEnum.FAMILY_ALL,configRequest,objFamily,server);
        return optional.isPresent() ? optional.get() : Collections.emptyList();
    }

    public boolean saveKVServerConfig(SetConfigKvRequest configRequest, Server server) {
        return this.configureKVHandleConsumer.saveKVServerConfig(configRequest,server);
    }
}
