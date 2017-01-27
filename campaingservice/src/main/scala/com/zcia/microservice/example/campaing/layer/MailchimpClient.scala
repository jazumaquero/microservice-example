package com.zcia.microservice.example.campaing.layer

trait MailchimpClient  extends BaseService with Protocol{

  def createList(list: MailchimpDomain.List) : String

  def createCampaign(campaign: MailchimpDomain.Campaign)
  def createCampaignContent(content: MailchimpDomain.Content)

  def addMembersToList(members: Seq[MailchimpDomain.Member])
  def addMemberToCampaing(member: MailchimpDomain.Member)

  def sendCampaing(campaign: MailchimpDomain.Campaign)
}
