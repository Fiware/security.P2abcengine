//* Licensed Materials - Property of IBM, Miracle A/S, and            *
//* Alexandra Instituttet A/S                                         *
//* eu.abc4trust.pabce.1.0                                            *
//* (C) Copyright IBM Corp. 2012. All Rights Reserved.                *
//* (C) Copyright Miracle A/S, Denmark. 2012. All Rights Reserved.    *
//* (C) Copyright Alexandra Instituttet A/S, Denmark. 2012. All       *
//* Rights Reserved.                                                  *
//* US Government Users Restricted Rights - Use, duplication or       *
//* disclosure restricted by GSA ADP Schedule Contract with IBM Corp. *
//*                                                                   *
//* This file is licensed under the Apache License, Version 2.0 (the  *
//* "License"); you may not use this file except in compliance with   *
//* the License. You may obtain a copy of the License at:             *
//*   http://www.apache.org/licenses/LICENSE-2.0                      *
//* Unless required by applicable law or agreed to in writing,        *
//* software distributed under the License is distributed on an       *
//* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY            *
//* KIND, either express or implied.  See the License for the         *
//* specific language governing permissions and limitations           *
//* under the License.                                                *
//*/**/****************************************************************

package eu.abc4trust.guice;

import com.google.inject.Module;
import com.google.inject.util.Modules;

import eu.abc4trust.guice.abcEngine.RealAbcEngineModule;
import eu.abc4trust.guice.configuration.AbceConfiguration;
import eu.abc4trust.guice.configuration.AbceConfigurationModule;
import eu.abc4trust.guice.configuration.DefaultAbceConfigurationModule;
import eu.abc4trust.guice.credCompressor.CredentialCompressorListModule;
import eu.abc4trust.guice.cryptoEngine.BridgedCryptoEngineWithIdemixIssuerModule;
import eu.abc4trust.guice.cryptoEngine.BridgedCryptoEngineWithUproveIssuerModule;
import eu.abc4trust.guice.cryptoEngine.IdemixCryptoEngineModule;
import eu.abc4trust.guice.cryptoEngine.MockCryptoEngineModule;
import eu.abc4trust.guice.cryptoEngine.UproveCryptoEngineModule;
import eu.abc4trust.guice.ui.MockUiModule;

public class ProductionModuleFactory {

    public enum CryptoEngine {
        IDEMIX(new IdemixCryptoEngineModule()),
        UPROVE(new UproveCryptoEngineModule()),
        BRIDGED(new BridgedCryptoEngineWithIdemixIssuerModule()),
        BRIDGED_WITH_IDEMIX_ISSUER(new BridgedCryptoEngineWithIdemixIssuerModule()),
        BRIDGED_WITH_UPROVE_ISSUER(new BridgedCryptoEngineWithUproveIssuerModule()),
        MOCK(new MockCryptoEngineModule());

        private final Module module;

        CryptoEngine(Module module) {
            this.module = module;
        }

        Module getModule() {
            return this.module;
        }
    }

    public static Module newModule() {
        return newModule(CryptoEngine.IDEMIX, new DefaultAbceConfigurationModule());
    }

    public static Module newModule(CryptoEngine ce) {
        return newModule(ce, new DefaultAbceConfigurationModule());
    }

    public static Module newModule(AbceConfiguration configuration, CryptoEngine ce) {
        return newModule(ce, new AbceConfigurationModule(configuration));
    }

    private static Module newModule(CryptoEngine ce, Module configurationModule) {
        /*
         * Note the "Singleton" scope means that you can create only one instance of the class
         * per injector. In this way we make sure that Guice creates only one of each class.
         * 
         * If you want several disjoint instances, you should create a new injector.
         * For example:
         *     Injector injector1 = Guice.createInjector(ProductionModuleFactory.newModule());
         *     UserAbcEngine engine1 = injector1.getInstance(UserAbcEngine.class);
         *     UserAbcEngine engine2 = injector1.getInstance(UserAbcEngine.class);
         *     Injector injector3 = Guice.createInjector(ProductionModuleFactory.newModule());
         *     UserAbcEngine engine3 = injector3.getInstance(UserAbcEngine.class);
         * engine1 and engine2 point to the same object (and object graph),
         * while engine3 is a different object (and object graph).
         * Furthermore all objects in engine1 and engine3 are different (for example the
         * KeyManager will be different).
         */
        return Modules.combine(configurationModule,
                new RealAbcEngineModule(),
                new MockUiModule(),
                new CredentialCompressorListModule(),
                ce.getModule());
    }

}
