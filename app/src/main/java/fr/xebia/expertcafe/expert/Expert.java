package fr.xebia.expertcafe.expert;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import fr.xebia.expertcafe.common.AppConstant;
import fr.xebia.expertcafe.common.ParseConstant;

import static fr.xebia.expertcafe.common.ParseConstant.DESCRIPTION;
import static fr.xebia.expertcafe.common.ParseConstant.EXPERT_DOMAIN;
import static fr.xebia.expertcafe.common.ParseConstant.PICTURE;

@ParseClassName(ParseConstant.EXPERT)
public class Expert extends ParseObject {

    public void setDomain(AppConstant.Domain domain) {
        put(EXPERT_DOMAIN, domain.name());
    }

    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }

    public void setPicture(ParseFile picture) {
        put(PICTURE, picture);
    }

    public AppConstant.Domain getDomain() {
        return AppConstant.Domain.from(getString(EXPERT_DOMAIN));
    }

    public String getDescription() {
        return getString(ParseConstant.DESCRIPTION);
    }

    public ParseFile getPicture() {
        return getParseFile(ParseConstant.PICTURE);
    }

}
