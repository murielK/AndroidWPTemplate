/*
 *
 *  *
 *  *  * ****************************************************************************
 *  *  * Copyright (c) 2015. Muriel Kamgang Mabou
 *  *  * All rights reserved.
 *  *  *
 *  *  * This file is part of project AndroidWPTemplate.
 *  *  * It can not be copied and/or distributed without the
 *  *  * express permission of Muriel Kamgang Mabou
 *  *  * ****************************************************************************
 *  *
 *  *
 *
 */

package hr.mk.wpmagazine.android.component.iterator;

/**
 * Created by Mur0 on 5/30/2015.
 */
public interface DataProvider {

    void queryAll();

    void queryArticles(final String field, final boolean filters);

    void queryArticles(final String field, final String... filters);

    void queryArticlesContains(final String field, final String filters);

}
