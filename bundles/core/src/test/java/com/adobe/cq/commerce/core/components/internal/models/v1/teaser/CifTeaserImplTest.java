/*******************************************************************************
 *
 *    Copyright 2019 Adobe. All rights reserved.
 *    This file is licensed to you under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License. You may obtain a copy
 *    of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software distributed under
 *    the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 *    OF ANY KIND, either express or implied. See the License for the specific language
 *    governing permissions and limitations under the License.
 ******************************************************************************/
package com.adobe.cq.commerce.core.components.internal.models.v1.teaser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.adobe.cq.commerce.core.components.models.teaser.CifTeaser;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CifTeaserImplTest {

    private CifTeaser slingModel;

    final private String productPath = "/content/test-product-page";
    final private String categoryPath = "/content/test-category-page";

    @Before
    public void setup() {
        CifTeaserImpl cifTeaser = new CifTeaserImpl();

        Page productPage = mock(Page.class);
        Page categoryPage = mock(Page.class);

        when(productPage.getPath()).thenReturn(productPath);
        when(categoryPage.getPath()).thenReturn(categoryPath);

        List<Resource> actionResources = new ArrayList<>();// mock(Resource.class);

        // Action node 1 - productSKU configured, category left blank, text set to some value
        actionResources.add(getActionNodeResource("278", null, "My Product"));

        // Action node 2 - productSKU left blank, category configured, text set to some value
        actionResources.add(getActionNodeResource(null, "30", "My Category"));

        // Action node 3 - productSKU configured, category also configured, text set to some value
        actionResources.add(getActionNodeResource("278", "30", "My Category"));

        // Action node 4 - productSKU left blank, category also left blank, text set to some value
        actionResources.add(getActionNodeResource(null, null, "This Page"));

        Resource mockedResource = mock(Resource.class);
        Resource mockedChildResource = mock(Resource.class);

        when(mockedChildResource.getChildren()).thenReturn(actionResources);

        when(mockedResource.getChild(CifTeaser.NN_ACTIONS)).thenReturn(mockedChildResource);

        // cifTeaser.testHelper(mockedResource, productPage, categoryPage);

        // Whitebox.setInternalState(this.slingModel, "category", categoryQueryResult);

        Whitebox.setInternalState(cifTeaser, "resource", mockedResource);
        Whitebox.setInternalState(cifTeaser, "categoryPage", categoryPage);
        Whitebox.setInternalState(cifTeaser, "productPage", productPage);
        Whitebox.setInternalState(cifTeaser, "actionsEnabled", true);

        cifTeaser.populateActions();
        this.slingModel = cifTeaser;

    }

    Resource getActionNodeResource(String sku, String categoryId, String text) {
        Resource actionResource = mock(Resource.class);

        Map<String, Object> actionProperties = new HashMap<>();

        actionProperties.put(CifTeaser.PN_ACTION_PRODUCT_SKU, sku);
        actionProperties.put(CifTeaser.PN_ACTION_CATEGORY_ID, categoryId);
        actionProperties.put(CifTeaser.PN_ACTION_TEXT, text);

        ValueMapDecorator vMD = new ValueMapDecorator(actionProperties);

        when(actionResource.getValueMap()).thenReturn(vMD);

        return actionResource;
    }

    @Test
    public void verifyActions() {

        List<ListItem> actionItems = this.slingModel.getActions();

        Assert.assertTrue(this.slingModel.isActionsEnabled());
        Assert.assertTrue(actionItems.size() == 4);

        Assert.assertTrue(actionItems.get(0).getURL().equalsIgnoreCase(this.productPath + ".278.html"));
        Assert.assertTrue(actionItems.get(1).getURL().equalsIgnoreCase(this.categoryPath + ".30.html"));
        Assert.assertTrue(actionItems.get(2).getURL().equalsIgnoreCase(this.categoryPath + ".30.html"));

    }
}
