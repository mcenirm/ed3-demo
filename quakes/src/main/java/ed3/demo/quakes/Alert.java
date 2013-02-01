package ed3.demo.quakes;

import com.sun.syndication.feed.impl.ObjectBean;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "urn:oasis:names:tc:emergency:cap:1.1")
@XmlType(propOrder = {
    "identifier",
    "sender",
    "sent",
    "status",
    "msgType",
    "scope",
    "info"
})
public class Alert {

    public static final String APPLICATION_CAP_XML = "application/cap+xml";
    private ObjectBean _objBean;
    private String identifier;
    private String sender;
    private Date sent;
    private String status;
    private String msgType;
    private String scope;
    private Info info;
    private Date publishedDate;

    @XmlType(propOrder = {
        "category",
        "event",
        "urgency",
        "severity",
        "certainty",
        "eventCode",
        "headline",
        "description",
        "web",
        "area"
    })
    public static class Info {

        private Alert _alert;
        private String category;
        private String event;
        private String urgency;
        private String severity;
        private String certainty;
        private EventCode eventCode;
        private String headline;
        private String description;
        private String web;
        private Area area;

        @XmlType(propOrder = {
            "valueName",
            "value"
        })
        public static class EventCode {

            private String valueName;
            private String value;

            public String getValueName() {
                return valueName;
            }

            public void setValueName(String valueName) {
                this.valueName = valueName;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        @XmlType(propOrder = {
            "areaDesc",
            "polygon"
        })
        public static class Area {

            private Info _info;
            private double latitude;
            private double longitude;

            public Area(Info info) {
                this._info = info;
            }

            @XmlElement
            public String getAreaDesc() {
                return "" + latitude + "," + longitude;
            }

            //@XmlElement
            public String getCircle() {
                return getAreaDesc() + " 0";
            }

            @XmlElement
            public String getPolygon() {
                return getAreaDesc() + " " + getAreaDesc() + " " + getAreaDesc() + " " + getAreaDesc();
            }

            public void setPoint(double latitude, double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }

        public Info(Alert alert) {
            this._alert = alert;
            this.eventCode = new EventCode();
            this.area = new Area(this);
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getUrgency() {
            return urgency;
        }

        public void setUrgency(String urgency) {
            this.urgency = urgency;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getCertainty() {
            return certainty;
        }

        public void setCertainty(String certainty) {
            this.certainty = certainty;
        }

        @XmlElement
        public EventCode getEventCode() {
            return eventCode;
        }

        private void setEventCode(String name, String value) {
            this.getEventCode().setValueName(name);
            this.getEventCode().setValue(value);
        }

        public String getHeadline() {
            return headline;
        }

        public void setHeadline(String headline) {
            this.headline = headline;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getWeb() {
            return web;
        }

        public void setWeb(String web) {
            this.web = web;
        }

        @XmlElement
        public Area getArea() {
            return area;
        }

        public void setAreaPoint(double latitude, double longitude) {
            this.getArea().setPoint(latitude, longitude);
        }
    }

    public Alert() {
        _objBean = new ObjectBean(Alert.class, this);
        info = new Info(this);
    }

    @Override
    public String toString() {
        return _objBean.toString();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getSent() {
        return sent;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @XmlElement
    public Info getInfo() {
        return info;
    }

    public void setInfoWeb(String web) {
        this.info.setWeb(web);
    }

    public void setInfoCategory(String category) {
        this.info.setCategory(category);
    }

    public void setInfoEvent(String event) {
        this.info.setEvent(event);
    }

    public void setInfoUrgency(String urgency) {
        this.info.setUrgency(urgency);
    }

    public void setInfoSeverity(String severity) {
        this.info.setSeverity(severity);
    }

    public void setInfoCertainty(String certainty) {
        this.info.setCertainty(certainty);
    }

    public void setInfoEventCode(String name, String value) {
        this.info.setEventCode(name, value);
    }

    public void setInfoHeadline(String headline) {
        this.info.setHeadline(headline);
    }

    public void setInfoDescription(String description) {
        this.info.setDescription(description);
    }

    public void setInfoAreaPoint(double latitude, double longitude) {
        this.info.setAreaPoint(latitude, longitude);
    }

    @XmlTransient
    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
}
