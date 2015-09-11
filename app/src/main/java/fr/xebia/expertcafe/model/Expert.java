package fr.xebia.expertcafe.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import fr.xebia.expertcafe.common.AppConstant;
import fr.xebia.expertcafe.common.ParseConstant;

import static fr.xebia.expertcafe.common.ParseConstant.DESCRIPTION;
import static fr.xebia.expertcafe.common.ParseConstant.DOMAIN;
import static fr.xebia.expertcafe.common.ParseConstant.EXPERT_TABLE;
import static fr.xebia.expertcafe.common.ParseConstant.PICTURE;

@ParseClassName(EXPERT_TABLE)
public class Expert extends ParseObject {

    public void setDomain(AppConstant.Domain domain) {
        put(DOMAIN, domain.name());
    }

    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }

    public void setPicture(ParseFile picture) {
        put(PICTURE, picture);
    }

    public AppConstant.Domain getDomain() {
        return AppConstant.Domain.from(getString(DOMAIN));
    }

    public String getDescription() {
        return getString(ParseConstant.DESCRIPTION);
    }

    public ParseFile getPicture() {
        return getParseFile(ParseConstant.PICTURE);
    }

}
