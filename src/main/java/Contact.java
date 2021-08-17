
public class Contact {
    private final String customerId;
    private final String companyName;
    private final String contactName;
    private final String contactTitle;
    private final String address;
    private final String city;
    private final String email;
    private final String region;
    private final String postalCode; // in case of ZIP+4 codes
    private final String country;
    private final String phone;
    private final String fax;

    private Contact(Builder builder){
        customerId = builder.customerId;
        companyName = builder.companyName;
        contactName = builder.contactName;
        contactTitle = builder.contactTitle;
        address = builder.address;
        city = builder.city;
        email = builder.email;
        region = builder.region;
        postalCode = builder.postalCode;
        country = builder.country;
        phone = builder.phone;
        fax = builder.fax;
        System.out.println(this);
        System.out.println();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public String getRegion(){
        return region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getPhone() {
        return phone;
    }


    public String getFax() {
        return fax;
    }

    @Override
    public String toString(){
        return "Customer ID: " + customerId +
                "\nCompany Name: " + companyName +
                "\nEmail: " + email;
    }
    public static class Builder {
        private final String customerId;
        private String companyName = "";
        private String contactName = "";
        private String contactTitle = "";
        private String address = "";
        private String city = "";
        private String email = "";
        private String region = "";
        private String postalCode = ""; // in case of ZIP+4 codes
        private String country = "";
        private String phone = "";
        private String fax = "";

        public Builder(String customerId){
            this.customerId = customerId;
        }

        public Builder companyName(String value){
            companyName = value;
            return this;
        }

        public Builder contactName(String value){
            contactName = value;
            return this;
        }
        public Builder contactTitle(String value){
            contactTitle = value;
            return this;
        }

        public Builder address(String value){
            address = value;
            return this;
        }
        public Builder email(String value){
            email = value;
            return this;
        }

        public Builder region(String value){
            region = value;
            return this;
        }

        public Builder city(String value){
            city = value;
            return this;
        }

        public Builder postalCode(String value){
            postalCode = value;
            return this;
        }

        public Builder country(String value){
            country = value;
            return this;
        }

        public Builder phone(String value){
            phone = value;
            return this;
        }

        public Builder fax(String value){
            fax = value;
            return this;
        }

        public Contact build(){
            return new Contact(this);
        }
    }
}
